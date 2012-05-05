import sys
import random

if __name__ == '__main__':
    
    if len(sys.argv) != 2:
        print "Please input the N"
        sys.exit(1)
    
    attrNum = int(sys.argv[1]);
    
    posLeft = 1.0
    negLeft = -1.0

    attrList =[]
    
    for i in range(attrNum-2):
        attr = random.uniform(negLeft/2,posLeft/2)
        val = "%.2f" %attr;
        val = int(val)
        if(val > 0):
            posLeft -= val
        if(val < 0):
            negLeft -= val
        attrList.append(val);

    val = round(posLeft,2)
    attrList.append(val);
    val = round(negLeft,2)
    attrList.append(val);

    print "weights:"
    for tmp in attrList:
        print tmp

