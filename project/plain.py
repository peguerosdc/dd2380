#!/bin/python2
# -*- coding: utf-8 -*-
import json

import sys

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
cfdist = ConditionalFreqDist()

with open(training) as training_file:
    for line in training_file:
        n_plus_one_grams = list(ngrams(tknzr.tokenize(line), N_SIZE + 1)) # NOTE: already normalized
        for n_plus_one_gram in n_plus_one_grams:
            n_gram = n_plus_one_gram[:-1]
            next_word = n_plus_one_gram[-1]
            cfdist[n_gram][next_word] += 1

tot = 0.0
correct = 0.0
nopred = 0.0
with open(validation) as validation_file:
    for line in validation_file:
        toks = tknzr.tokenize(line)
        for i in range(N_SIZE+1, len(toks)):
            input_toks = toks[:i-1]
            expect_tok = toks[i-1]
            #print("t" + str(toks[:i]))

            n_grams = list(ngrams(input_toks, N_SIZE))
            last_n_gram = n_grams[-1]
            #print("a" + str(n_grams))

            predicted = None
            try:
                mc = cfdist[last_n_gram].most_common()
                if len(mc) != 0:
                    predicted = mc[0][0]
            except IndexError:
                print >> sys.stderr, ("ERROR: the minimum length of the input must be " + str(N_SIZE))
            except ValueError:
                pass
            tot += 1
            if predicted == None:
                nopred += 1
            else:
                if expect_tok in predicted:
                    correct += 1
                #else: print(input_toks, predicted, expect_tok)

print("tot: " + str(tot))
print("correct: " + str(correct))
print("nopred: " + str(nopred))
print("nopred/tot: " + str(nopred/tot))
print("RESULT: " + str(correct/tot))
