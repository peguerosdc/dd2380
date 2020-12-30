#!/bin/python3

import sys
import matplotlib.pyplot as plt
import itertools

correct_rate = {
    "mega" : {
        "e": [(2,0.467387295373), (3,0.534162638175), (4,0.550666263887), (5,0.556478283237), (6,0.562222042533), (7,0.567325758254)],
        "c": [(2,0.444633585837), (3, 0.527573359608), (4, 0.53395092807), (5, 0.511868891012), (6, 0.476995229239), (7, 0.441218573857)]
    },
    "mega2" : {
        "e": [(2, 0.477733021605), (3, 0.553224672028), (4, 0.570471141342), (5, 0.575826182217), (6, 0.580427175094), (7, 0.5849703773)],
        "c": [(2, 0.453592856539), (3, 0.544570502431), (4, 0.553832816512), (5, 0.529961657842), (6, 0.49197538313), (7, 0.457035948149)]
    },
    "mega3" : {
        "e": [(2, 0.471297359357), (3, 0.549928228172), (4, 0.567365014478), (5, 0.57336621455), (6, 0.579485066941), (7, 0.584680734448)],
        "c": [(2, 0.450574052813), (3, 0.54356237908), (4, 0.550706864248), (5, 0.52908866719), (6, 0.492605561277), (7, 0.458801498127)]
    },

    "ultramega1" : {
        "e": [(2, 0.436149717903), (3, 0.508266882912), (4, 0.517105133186), (5, 0.520061672411), (6, 0.522088837948), (7, 0.523408071749)],
        "c": [(2, 0.413279207376), (3, 0.49913651995), (4, 0.505383424978), (5, 0.486214402322), (6, 0.450785224508), (7, 0.41302690583)]
    },
    "ultramega2" : {
        "e": [(2, 0.442092723671), (3, 0.513044267328), (4, 0.52284685223), (5, 0.527042366985), (6, 0.529381893522), (7, 0.531824161865)],
        "c": [(2, 0.419445098555), (3, 0.503395290452), (4, 0.512157878139), (5, 0.494452378337), (6, 0.459669720298), (7, 0.420246790364)]
    },
    "ultramega3" : {
        "e": [(2, 0.431547700849), (3, 0.505893114203), (4, 0.516398428381), (5, 0.519810944913), (6, 0.522179644082), (7, 0.52408749329)],
        "c": [(2, 0.408281340186), (3, 0.495906309981), (4, 0.505482582321), (5, 0.486653869834), (6, 0.452581104669), (7, 0.414139380927)]
    }
}

def get_xy(prefix, grade, max_n):
    return zip(*correct_rate[prefix][grade][:max_n-2])

#def plot_perc(prefix, max_n):
#    (x,y) = zip(*correct_rate[prefix]["e"][:max_n-2])
#    plt.plot(x,y, '--.', label="without grammar")
#    (x,y) = zip(*correct_rate[prefix]["c"][:max_n-2])
#    plt.plot(x,y, '--.', label="with grammar")
#    xmin,xmax = 1, 7
#    ymin,ymax = None, None
#    plt.axis([xmin,xmax,ymin,ymax])

#plot_perc("mega", 'r', 7)
#plot_perc("mega2", 'r', 7)
#plot_perc("mega3", 'r', 7)
#plot_perc("ultramega1", 7)
#plot_perc("ultramega2", 7)
#plot_perc("ultramega3", 7)

plt.plot(*get_xy("mega", "e", 7), color='r', linestyle=':', marker='o', label="s1")
plt.plot(*get_xy("mega2", "e", 7), color='g', linestyle=':', marker='o', label="s2")
plt.plot(*get_xy("mega3", "e", 7), color='b', linestyle=':', marker='o', label="s3")

plt.plot(*get_xy("ultramega1", "e", 7), color='y', linestyle=':', marker='o', label="b1")
plt.plot(*get_xy("ultramega2", "e", 7), color='c', linestyle=':', marker='o', label="b2")
plt.plot(*get_xy("ultramega3", "e", 7), color='m', linestyle=':', marker='o', label="b3")


plt.plot(*get_xy("mega", "c", 7), color='r', linestyle='-', marker='o', label="s1 g")
plt.plot(*get_xy("mega2", "c", 7), color='g', linestyle='-', marker='o', label="s2 g")
plt.plot(*get_xy("mega3", "c", 7), color='b', linestyle='-', marker='o', label="s3 g")

plt.plot(*get_xy("ultramega1", "c", 7), color='y', linestyle='-', marker='o', label="b1 g")
plt.plot(*get_xy("ultramega2", "c", 7), color='c', linestyle='-', marker='o', label="b2 g")
plt.plot(*get_xy("ultramega3", "c", 7), color='m', linestyle='-', marker='o', label="b3 g")

#plt.errorbar(np.arange(1,n+1)-0.1, y[::2], yerr=list(zip(*errs[::2])), fmt='r.', label="GA")

#plt.axes().set_aspect('equal', 'datalim')

xmin,xmax = 0, 7
ymin,ymax = None, None
plt.axis([xmin,xmax,ymin,ymax])

plt.xlabel('n-gram size (n) used when training')
plt.ylabel('number of correct predictions / total number of predictions')

plt.legend(loc='upper left')
plt.show()
