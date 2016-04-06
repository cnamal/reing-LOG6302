import sys, getopt
import linecache

fileName= None
lis = None
beginL = None
endL = None

def error():
    print('slic.py -f <inputfile> -l [lines] -b <beginLine> -e <endLine>')
    print('fileName is mandatory')
    print('if l is set, b and e cannot be set and vice versa')
    print('if b is set, e has to be set and vice versa')
    sys.exit(2)

try:
    opts, args = getopt.getopt(sys.argv[1:],"f:b:e:l:")
except getopt.GetoptError:
    error()

for opt, arg in opts:
    if opt == '-f':
        fileName = arg
    if opt == '-b':
        beginL = arg
    if opt == '-e':
        endL = arg
    if opt == '-l':
        lis =arg

if fileName is None:
    error()

if (beginL is None) and (endL is None) and (lis is None):
    error()

if ((beginL is not None) and (endL is None)) or ((beginL is None) and (endL is not None)):
    error()

if (lis is not None) and ((beginL is not None) or (endL is not None)):
    error()

if beginL is not None:
    fileRead = open(fileName,"r")
    curr = int(beginL)
    for line in fileRead.readlines()[int(beginL)-1:int(endL)]:
        sys.stdout.write(str(curr)+ " "+line)
        curr+=1
else:
    if lis[len(lis)-1] ==",":
        lis = lis[:len(lis)-1]

    lis= list(set(map(int, lis.split(","))))
    lis.sort()
    if lis[0] == -1:
        lis = lis[1:]
    for index in lis:
        sys.stdout.write(linecache.getline(fileName,index))
