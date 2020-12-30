#!/bin/python2
# -*- coding: utf-8 -*-
import json
import pickle
import sys
import operator

import nltk
from nltk.tokenize import TweetTokenizer
from nltk.probability import ConditionalFreqDist
from nltk.util import ngrams

argv = sys.argv[1:]

try:
    if len(argv) != 2:
        raise ValueError()
    result = argv[0]
    validation = argv[1]
except ValueError:
    print("Usage: python2 [RESULT] [VALIDATION]")
    sys.exit(2)

tknzr = TweetTokenizer(strip_handles=True, reduce_len=True)

tot = 0.0
correct = 0.0
with open(result) as result_file:
    with open(validation) as validation_file:
        result_lines = result_file.readlines()
        validation_lines = validation_file.readlines()

        for x,y in zip(result_lines, validation_lines):
            x = x[:-1] # remove '\n'
            (n_gram, count) = eval(y)

            tot += 1
            if x == n_gram[-1]:
                correct += 1

print(correct/tot)
