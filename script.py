from os import listdir
from os.path import isfile, join
mypath="graphs"
onlyfiles = [f for f in listdir(mypath) if isfile(join(mypath, f)) and f.endswith(".dot")]
basename=[]
for f in onlyfiles:
    basename.append(f[:f.rfind(".")])
packages = {}
for f in basename:
    index = f.rfind(".");
    if(index>=0):
        package = f[:index]
        if package not in packages:
            packages[package]=[]
        packages[package].append(mypath+"/"+f+".dot");
for package in packages:
    with open(mypath+"/"+package+".dot",'w') as fpack:
        fpack.write("digraph G{")
        for f in packages[package]:
            with open(f, 'r') as ffile:
                data = ffile.read().splitlines(True)
            fpack.writelines(data[1:-1])
        fpack.write("}")
