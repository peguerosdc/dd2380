#!/bin/python2
# -*- coding: utf-8 -*-
# import json
import pickle
import sys
import operator

import nltk
from nltk.tokenize import TweetTokenizer
# from nltk.probability import ConditionalFreqDist
from nltk.util import ngrams

AGENTS_PATH = "./agents/"

# Required to run once and get the data.
# nltk.download()
# Parse the length of the n-grams
if len(sys.argv) <= 1:
    print("Usage: python c_grade.py <N_VALUE>")
    sys.exit(2)
try:
    N_SIZE = int(sys.argv[1])
except ValueError:
    print("Usage: python c_grade.py <N_VALUE>")
    sys.exit(2)
# Load agents
print("Trying to load trained agents... "),
sys.stdout.flush()
try:
    wagent_file = open(AGENTS_PATH + "words_agent_for_" + str(N_SIZE) +
                       '.pkl', 'r')
    words_agent = pickle.load(wagent_file)
    wagent_file.close()

    gagent_file = open(AGENTS_PATH + "grammar_agent_for_" + str(N_SIZE) +
                       '.pkl', 'r')
    grammar_agent = pickle.load(gagent_file)
    gagent_file.close()
except IOError:
    print("")
    print(u"There's no agent trained for this N value")
    sys.exit(2)
print("done!")


def normalize(tokenized):
    normalized = []
    for t in tokenized:
        # Remove smiles, URLs, hashtags, mentions and retweets
        if t[0] in (u'.', u':', u'&', u',', u'"', u"'", u"#") or \
           t.startswith(u"http") or \
           t.startswith(u"@") or t == u"rt":
            continue
        normalized.append(t)
    return normalized

# Python2.X compat
try:
    input = raw_input
except NameError:
    pass

# How many results to show
showres = 2

# Init
tknzr = TweetTokenizer(strip_handles=True, reduce_len=True)

while True:
    # Read input
    try:
        user_input = input("Input: ")
        user_input = user_input.lower()
    except EOFError:
        break
    if user_input == "exit":
        break
    # Tokenize
    tokenized = tknzr.tokenize(user_input)
    # Normalize
    normalized = normalize(tokenized)
    # PoS tag
    tagged = [t[1] for t in nltk.pos_tag(normalized, tagset='universal')]

    # Build the n-gram that are going to be used for prediction
    n_grams = list(ngrams(normalized, N_SIZE))
    # Build the grammar n-grams
    grammar_n_grams = list(ngrams(tagged, N_SIZE))
    print("======== RESULTS ========")
    try:
        # Show which are the possible types of the next word
        grammar_distr = grammar_agent[' '.join(grammar_n_grams[-1])]
        next_types = dict()
        for item in grammar_distr.most_common():
            next_types[item[0][0]] = grammar_distr.freq(item[0])
        print("Next word must be a:")
        for nt in next_types:
            print("%s : %s" % (nt, next_types[nt]))

        # Get the distribution for this n_gram
        words_distr = words_agent[' '.join(n_grams[-1])]

        # Compute the probabilities of the C predictor as:
        # probabilty_of_type * probability_of_word
        # TODO: is this correct?
        print("======== PREDICTIONS ========")
        most_common = dict()
        for (word_tuple, count) in words_distr.most_common():
            this_tag = nltk.pos_tag([word_tuple[0]], tagset='universal')[0][1]
            try:
                most_common[word_tuple[0]] = next_types[this_tag] * \
                    words_distr.freq(word_tuple)
            except KeyError:
                pass

        # Show predictions for the C and E agents
        if len(most_common) == 0:
            print("Couldn't make a prediction")
        else:
            print("** C - Grade **")
            c_predictions = sorted(most_common.items(),
                                   key=operator.itemgetter(1), reverse=True)
            for p in c_predictions[:showres]:
                print("- %s : %s" % (p[0], p[1]))
            print("** E - Grade **")
            for (word_tuple, count) in words_distr.most_common()[:showres]:
                print("- " + word_tuple[0] + " : " +
                      str(words_distr.freq(word_tuple)))
    except IndexError:
        print("ERROR: the minimum length of the input must be " + str(N_SIZE))
    except ValueError:
        print("ERROR: no word found!")
    print("")
