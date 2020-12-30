#!/bin/python2
# -*- coding: utf-8 -*-
import json

import sys
import nltk
import pickle
from nltk.tokenize import TweetTokenizer
from nltk.probability import ConditionalFreqDist
from nltk.util import ngrams

# Download the PoS-tagger data when needed (only the first time)
# Models/maxent_treebank_pos_tagger
# Models/universal_tagset
# nltk.download()

AGENTS_PATH = "./agents/"
CORPUS_SOURCE = ["./twitter_samples/corpus_141015.json",
                 "./twitter_samples/positive_tweets.json",
                 "./twitter_samples/negative_tweets.json",
                 "./twitter_samples/tweets.20150430-223406.json"]
tknzr = TweetTokenizer(strip_handles=True, reduce_len=True)

def normalize(tokenized):
    normalized = []
    for t in tokenized:
        # Remove smiles, URLs, hashtags, mentions and retweets
        if t[0] in (u'.', u':', u'&', u',', u'"', u"'", u"#") or \
           t.startswith(u"http") or \
           t.startswith(u"(") or t.endswith(u")") or \
           t.startswith(u"@") or t == u"rt":
            continue
        normalized.append(t)
    return normalized

# Create one agent for the each of the next n-sizes:
ns = [1, 2, 3, 4]
words_agents = [ConditionalFreqDist() for N in ns]
grammar_agents = [ConditionalFreqDist() for N in ns]

# Read corpus
for corpus in CORPUS_SOURCE:
    print("Training with " + corpus + "... "),
    sys.stdout.flush()
    with open(corpus) as corpus_file:
        for line in corpus_file:
            try:
                text = json.loads(line)["text"].lower()
            except KeyError, e:
                continue
            # Tokenize
            tokenized = tknzr.tokenize(text)
            # which unwanted words should we remove?
            normalized = normalize(tokenized)
            # PoS tag depending
            tagged = [t[1] for t in nltk.pos_tag(normalized, tagset='universal')]
            # Split in n-grams
            for N, words_agent, grammar_agent in zip(ns, words_agents, grammar_agents):
                n_grams = list(ngrams(normalized, N + 1))
                grammar_n_grams = list(ngrams(tagged, N + 1))
                # Which word/n-gram comes after which n-gram?
                for n_gram, grammar_n_gram in zip(n_grams,grammar_n_grams):
                    # Train words agent
                    prev_ngram = ' '.join(n_gram[:-1])
                    next_word = n_gram[-1:]
                    words_agent[prev_ngram][next_word] += 1
                    # Train grammar agent
                    prev_grammar = ' '.join(grammar_n_gram[:-1])
                    next_grammar = grammar_n_gram[-1:]
                    grammar_agent[prev_grammar][next_grammar] += 1
    print("done!")
# Store these trained agents in files
for N, words_agent, grammar_agent in zip(ns, words_agents, grammar_agents):
    words_filename = "words_agent_for_"+str(N)
    words_agent_file = open(AGENTS_PATH + words_filename+'.pkl', 'w')
    pickle.dump(words_agent, words_agent_file)
    words_agent_file.close()

    grammar_filename = "grammar_agent_for_"+str(N)
    grammar_agent_file = open(AGENTS_PATH + grammar_filename+'.pkl', 'w')
    pickle.dump(grammar_agent, grammar_agent_file)
    grammar_agent_file.close()
