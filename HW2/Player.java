import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

class Player {
    private static final int SEQUENCE_MIN_LENGTH = 66;
    private static final int COUNT_STATES = 5;
    // .8 - .7
    private static final double SAFE_TO_SHOOT = 0.8;
    private static final double SAFE_TO_GUESS = 0.0;

    int shootSuccessCount = 0;
    int shootsCount = 0;
    int guessCount;
    private int[] myguesses;
    long timeStep = 0;

    public static final Action cDontShoot = new Action(-1, -1);
    public static final int[] species = new int[]{
        Constants.SPECIES_PIGEON     ,
        Constants.SPECIES_RAVEN      ,
        Constants.SPECIES_SKYLARK    ,
        Constants.SPECIES_SWALLOW    ,
        Constants.SPECIES_SNIPE      ,
        Constants.SPECIES_BLACK_STORK
    };

    private ArrayList<HMM>[] speciesBehaviour;

    public Player() {
        speciesBehaviour = (ArrayList<HMM>[])new ArrayList[Constants.COUNT_SPECIES];
        for(int i = 0; i < speciesBehaviour.length; i++)
            speciesBehaviour[i] = new ArrayList<HMM>();
    }

    /**
     * Shoot!
     *
     * This is the function where you start your work.
     *
     * You will receive a variable pState, which contains information about all
     * birds, both dead and alive. Each bird contains all past moves.
     *
     * The state also contains the scores for all players and the number of
     * time steps elapsed since the last time this function was called.
     *
     * @param pState the GameState object with observations etc
     * @param pDue time before which we must have returned
     * @return the prediction of a bird we want to shoot at, or cDontShoot to pass
     */
    public Action shoot(GameState pState, Deadline pDue) {
        /*
         * Here you should write your clever algorithms to get the best action.
         * This skeleton never shoots.
         */

        // Let SEQUENCE_MIN_LENGTH turns pass in order to estimate an accurate HMM
        if(pState.getBird(0).getSeqLength() < SEQUENCE_MIN_LENGTH) return cDontShoot;
        if( timeStep == 0) {
            System.err.println("======== ROUND "+pState.getRound()+"=========");
            timeStep++;
        }
        
        int numBirds = pState.getNumBirds();
        // Create and train a new HMM for each alive bird and store the best model
        HMM[] models = new HMM[numBirds];
        int mostStableIndex = 0;
        double bestStability = -1.0;
        for(int i = 0; i < numBirds; i++) {
            Bird bird = pState.getBird(i);
            // TODO avoid Black Storks or if I'm not sure the specie of the bird
            int birdId = identifyBird(bird);
            if( bird.isDead()
                || birdId == Constants.SPECIES_BLACK_STORK
                || birdId == Constants.SPECIES_UNKNOWN ) continue;
            // Init this HMM
            models[i] = new HMM(COUNT_STATES, Constants.COUNT_MOVE);
            // Train each model with the observations so far
            int[] observations = getObservations(bird);
            double stability = models[i].estimateModel(observations);
            if( stability > bestStability) {
                mostStableIndex = i;
                bestStability = stability;
            }
        }
        if(bestStability == -1.0) return cDontShoot;

        // Pick most stable HMM and try to find its next move
        HMM mostStableHMM = models[mostStableIndex];
        Bird mostStableBird = pState.getBird(mostStableIndex);
        // Get the observations that this HMM suggests to estimate the states sequence until this point
        int[] observations = getObservations(mostStableBird);
        int[] states = mostStableHMM.estimateStateSequence(observations);
        // Init the current distribution according to the estimated sequence
        int mostProbState = states[states.length - 1];
        double[] currentDistribution = new double[COUNT_STATES];
        currentDistribution[ mostProbState ] = 1.0;
        // Get next observations according to the HMM
        double[] nextObservationsProbs = mostStableHMM.estimateProbabilityDistributionOfNextEmission(currentDistribution);
        // Pick the highest possible observation of the distribution
        int mostLikelyObservation = -1;
        double bestObservationProb = -1.0;
        for(int i = 0; i < nextObservationsProbs.length; i++) {
            if( nextObservationsProbs[i] > bestObservationProb) {
                bestObservationProb = nextObservationsProbs[i];
                mostLikelyObservation = i;
            }
        }
        // Only shoot if we are confident enough that we know the birds move
        if( bestObservationProb >= SAFE_TO_SHOOT) {
            shootsCount++;
            return new Action(mostStableIndex, mostLikelyObservation); 
        }
        else
            return cDontShoot;
    }

    /**
     * Guess the species!
     * This function will be called at the end of each round, to give you
     * a chance to identify the species of the birds for extra points.
     *
     * Fill the vector with guesses for the all birds.
     * Use SPECIES_UNKNOWN to avoid guessing.
     *
     * @param pState the GameState object with observations etc
     * @param pDue time before which we must have returned
     * @return a vector with guesses for all the birds
     */
    public int[] guess(GameState pState, Deadline pDue) {
        /*
         * Here you should write your clever algorithms to guess the species of
         * each bird. This skeleton makes no guesses, better safe than sorry!
         */
        int[] myGuess = new int[pState.getNumBirds()];
        myguesses= new int[pState.getNumBirds()];
        Random rand = new Random();
        // Sacrifice some points on the first round to get the behaviour of each bird
        if(pState.getRound() == 0) {
            for (int i = 0; i < pState.getNumBirds(); ++i) {
                myGuess[i] = species[i%Constants.COUNT_SPECIES];
                myguesses[i] = species[i%Constants.COUNT_SPECIES];
            }
        }
        // With an estimation of each birds behaviour, lets try to guess
        else {
            for (int i = 0; i < pState.getNumBirds(); ++i) {
                Bird bird = pState.getBird(i);
                // Which specie I think it is?
                myGuess[i] = identifyBird(bird);
                // TODO try several cases and see how I score more:
                // If its unknown, then dont guess
                // If its unknown, then guess a random one to add this beaviour to the training
                if(myGuess[i] == Constants.SPECIES_UNKNOWN) {
                    myGuess[i] = rand.nextInt((Constants.SPECIES_BLACK_STORK) + 1) + 0;
                }
                myguesses[i] = myGuess[i];
            }
        }
        return myGuess;
    }

    /**
     * If you hit the bird you were trying to shoot, you will be notified
     * through this function.
     *
     * @param pState the GameState object with observations etc
     * @param pBird the bird you hit
     * @param pDue time before which we must have returned
     */
    public void hit(GameState pState, int pBird, Deadline pDue) {
        // System.err.println("HIT BIRD "+pBird+"!!!");
        shootSuccessCount++;
    }

    /**
     * If you made any guesses, you will find out the true species of those
     * birds through this function.
     *
     * @param pState the GameState object with observations etc
     * @param pSpecies the vector with species
     * @param pDue time before which we must have returned
     */
    public void reveal(GameState pState, int[] pSpecies, Deadline pDue) {
        // Getting the results in this environment to have something to begin with
        guessCount = 0;
        if (pState.getRound() == 0) {
            for(int i = 0; i < pSpecies.length; i++) {
                // Estimate a HMM model according to this observations
                HMM newModel = new HMM(COUNT_STATES, Constants.COUNT_MOVE);
                int[] observations = getObservations(pState.getBird(i));
                newModel.estimateModel(observations);
                // Store new HMM
                speciesBehaviour[pSpecies[i]].add(newModel);
            }
        } 
        else {
            // Just store the behaviour if its a valid specie
            for(int i = 0; i < pSpecies.length; i++) {
                if( pSpecies[i] == myguesses[i] && myguesses[i] != Constants.SPECIES_UNKNOWN) {
                    guessCount++;
                }

                if(pSpecies[i] !=  Constants.SPECIES_UNKNOWN ) {
                    // Estimate a HMM model according to this observations
                    HMM newModel = new HMM(COUNT_STATES, Constants.COUNT_MOVE);
                    int[] observations = getObservations(pState.getBird(i));
                    newModel.estimateModel(observations);
                    // Store new HMM
                    speciesBehaviour[pSpecies[i]].add(newModel);
                }
            }
        }
        printStatistics(pSpecies.length);
    }


    private int[] getObservations(Bird bird) {
        int[] observations;
        // Get this birds observations
        if (bird.isAlive()) {
            observations = new int[bird.getSeqLength()];
            for(int o = 0; o < observations.length; o++) {
                observations[o] = bird.getObservation(o);
            }
        }
        // Just get observations before death
        else {
            ArrayList<Integer> aliveObservations = new ArrayList<Integer>();
            for(int i = 0 ; i < bird.getSeqLength(); i++) {
                if(bird.wasAlive(i))
                    aliveObservations.add(bird.getObservation(i));
            }
            // Parse to an array
            observations = new int[aliveObservations.size()];
            for(int i = 0 ; i < aliveObservations.size(); i++)
                observations[i] = aliveObservations.get(i);
        }
        return observations;
    }

    private int identifyBird(Bird bird) {
        int[] observations = getObservations(bird);
        // TODO add a minimum probability THRESHOLD
        double maxProbability = SAFE_TO_GUESS;
        int bestGuess = Constants.SPECIES_UNKNOWN;
        // Look for which specie, the stored HMM fits the best
        for(int specie = 0; specie < speciesBehaviour.length; specie++) {
            Iterator<HMM> it = speciesBehaviour[specie].iterator();
            while(it.hasNext()) {
                double probability = it.next().estimateProbabilityOfEmissionSequence(observations);
                if(probability > maxProbability) {
                    maxProbability = probability;
                    bestGuess = specie;
                }
            }
        }
        //System.err.println("Guess probability is "+maxProbability);
        return bestGuess;
    }

    private void printStatistics(int birdsAmount) {
        System.err.println("*STATS*");
        System.err.println("*Shoot success: "+shootSuccessCount+". Shoot failure: "+(shootsCount - shootSuccessCount));
        int guessesTried = 0;
        for(int i = 0; i < myguesses.length; i++)
            if( myguesses[i] != Constants.SPECIES_UNKNOWN) guessesTried++;
        System.err.println("*Guess success: "+guessCount+" of "+guessesTried);
        System.err.println("***");

        timeStep = 0;
    }

}
