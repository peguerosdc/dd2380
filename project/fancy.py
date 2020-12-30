#!/bin/python2
# -*- coding: utf-8 -*-
import json

import sys
import pickle
import nltk
import os.path
import operator

# import nltk
from nltk.tokenize import TweetTokenizer
from nltk.probability import ConditionalFreqDist
from nltk.util import ngrams

# Python2.X compat
try:
    input = raw_input
except NameError:
    pass

try:
    argv = sys.argv[1:]
    if len(argv) != 3:
        raise ValueError()
    N_SIZE = int(argv[0])
    training = argv[1]
    validation = argv[2]
except ValueError:
    print >> sys.stderr, ("Usage: python2 plain.py [N_SIZE] [TRAINING_FILE] [VALIDATION_FILE]")
    sys.exit(2)

tknzr = TweetTokenizer(strip_handles=True, reduce_len=True)
tagger = nltk.PerceptronTagger()
tagset='universal'

if os.path.isfile(training + '.w'+str(N_SIZE)+'_pkl') and os.path.isfile(training + '.g'+str(N_SIZE)+'_pkl'):
    print("found pickles")
    with open(training + '.w'+str(N_SIZE)+'_pkl', 'r') as word_agent_file:
        word_agent = pickle.load(word_agent_file)
    with open(training + '.g'+str(N_SIZE)+'_pkl', 'r') as gram_agent_file:
        gram_agent = pickle.load(gram_agent_file)
    print("ate pickles")
else:
    word_agent = ConditionalFreqDist()
    gram_agent = ConditionalFreqDist()

    print("training...")
    with open(training) as training_file:
        for line in training_file:
            words = tknzr.tokenize(line) # NOTE: already normalized
            #print(words)

            tagged = [t[1] for t in nltk.tag._pos_tag(words, tagset, tagger)]

            word_grams = list(ngrams(words, N_SIZE + 1))
            gram_grams = list(ngrams(tagged, N_SIZE + 1))

            for wg,gg in zip(word_grams, gram_grams):
                word_agent[wg[:-1]][wg[-1]] += 1
                gram_agent[gg[:-1]][gg[-1]] += 1
    print("done")

    print("puking pickles")
    with open(training + '.w'+str(N_SIZE)+'_pkl', 'w') as word_agent_file:
        pickle.dump(word_agent, word_agent_file)
    with open(training + '.g'+str(N_SIZE)+'_pkl', 'w') as gram_agent_file:
        pickle.dump(gram_agent, gram_agent_file)
    print("puked pickles")


tot = 0.0
correct = 0.0
nopred= 0.0
with open(validation) as validation_file:
    for line in validation_file:
        toks = tknzr.tokenize(line)
        for i in range(N_SIZE+1, len(toks)):
            input_toks = toks[:i-1]
            expect_tok = toks[i-1]

            tagged = [t[1] for t in nltk.tag._pos_tag(input_toks, tagset, tagger)]

            word_grams = list(ngrams(input_toks, N_SIZE))
            gram_grams = list(ngrams(tagged, N_SIZE))

            predicted = None
            try:
                gram_distr = gram_agent[gram_grams[-1]]
                next_types = dict()
                for item in gram_distr.most_common():
                    next_types[item[0]] = gram_distr.freq(item[0])

                word_distr = word_agent[word_grams[-1]]

                mc = dict()
                for (word, count) in word_distr.most_common():
                    this_tag = nltk.tag._pos_tag([word], tagset,tagger)[0][1]
                    try:
                        mc[word] = next_types[this_tag]*word_distr.freq(word)
                    except KeyError:
                        #print >> sys.stderr, ("ERROR: wot: "+repr(this_tag))
                        pass
                c_predictions = sorted(mc.items(), key=operator.itemgetter(1), reverse=True)
                if len(mc) != 0:
                    predicted = c_predictions[0][0]
            except IndexError:
                print >> sys.stderr, ("ERROR: the minimum length of the input must be " + str(N_SIZE))
            except ValueError:
                pass
            tot += 1
            if predicted == None:
                nopred += 1
            else:
                if predicted == expect_tok:
                    correct += 1
                #else: print(input_toks, predicted, expect_tok)

print("tot: " + str(tot))
print("correct: " + str(correct))
print("nopred: " + str(nopred))
print("nopred/tot: " + str(nopred/tot))
print("RESULT: " + str(correct/tot))
