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
    if len(argv) != 1:
        raise ValueError()
    corpus = argv[0]
except ValueError:
    print("Usage: python2 tok.py [TWEET_FILE]")
    sys.exit(2)

tknzr = TweetTokenizer(strip_handles=True, reduce_len=True)

corpus_dict = {}
with open(corpus) as corpus_file:
    for line in corpus_file:
        text = json.loads(line)["text"].lower()
        tokenized = tknzr.tokenize(text)
        normalized = []
        for t in tokenized:
            # Remove smiles, URLs, hashtags and mentions
            if t[0] in (u'.', u':', u'&', u',', u'"', u"'") or \
                any(ord(x)>=128 for x in t) or \
                t.startswith(u"http") or t.startswith(u"#") or \
                t.startswith(u"@") or t.startswith(u"rt"):
                continue
            normalized.append(t)
        print(' '.join(normalized))

