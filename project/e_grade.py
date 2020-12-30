#!/bin/python2
# -*- coding: utf-8 -*-
import json

# import nltk
from nltk.tokenize import TweetTokenizer
from nltk.probability import ConditionalFreqDist
from nltk.util import ngrams

# Python2.X compat
try:
    input = raw_input
except NameError:
    pass

# CORPUS_SOURCE = [ "./twitter_samples/positive_tweets.json"]
CORPUS_SOURCE = ["./twitter_samples/positive_tweets.json",
                 "./twitter_samples/negative_tweets.json",
                 "./twitter_samples/tweets.20150430-223406.json"]
N_SIZE = 3

# Init
tknzr = TweetTokenizer(strip_handles=True, reduce_len=True)
cfdist = ConditionalFreqDist()

# Read corpus
for corpus in CORPUS_SOURCE:
    print("Training with " + corpus + "... "),
    with open(corpus) as corpus_file:
        for line in corpus_file:
            text = json.loads(line)["text"].lower()
            # Tokenize
            tokenized = tknzr.tokenize(text)
            # TODO which unwanted words should we remove?
            normalized = []
            for t in tokenized:
                # Remove smiles, URLs, hashtags and mentions
                if t[0] in (u'.', u':', u'&', u',', u'"', u"'") or \
                   t.startswith(u"http") or t.startswith(u"#") or \
                   t.startswith(u"@"):
                    continue
                normalized.append(t)
            # Split in n-grams
            # TODO should we consider a case like: "how are you doing?"
            # and include a first n-gram like: (None, 'how', 'are') ???
            n_grams = list(ngrams(normalized, N_SIZE + 1))
            # Which word/n-gram comes after which n-gram?
            for n_gram in n_grams:
                prev_ngram = ' '.join(n_gram[:-1])
                next_word = n_gram[-1:]
                cfdist[prev_ngram][next_word] += 1
    print("done!")

# Start the prediction
# Cool accurate test cases for N_SIZE = 3:
# if you want... more
# if you want more... detail
# if you want more detail... please
# if you want more detail please... visit
# how are you... ?
# you need to... know
# you need to know... about
# you need to know about... david
# there is no... money
# there is no money... left
while True:
    # Read input
    try:
        user_input = input("Input: ")
        user_input = user_input.lower()
    except EOFError:
        break
    if user_input == "exit":
        break
    # Predict
    n_grams = list(ngrams(tknzr.tokenize(user_input), N_SIZE))
    print(n_grams)
    try:
        # print(cfdist[ ' '.join( n_grams[ -1 ] ) ].max())
        # Print best 3 (or available) options
        distr = cfdist[' '.join(n_grams[-1])]

        print(distr.most_common())

        mc = distr.most_common()
        if len(mc) == 0:
            print("Couldn't make a prediction")
        else:
            for (word_tuple, count) in mc[:3]:
                print(word_tuple[0] + " : " + str(distr.freq(word_tuple)))
        print("")
    except IndexError:
        print("ERROR: the minimum length of the input must be " + str(N_SIZE))
    except ValueError:
        print("ERROR: no word found!")
