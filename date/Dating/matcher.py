
from socket import *
from numpy import *
from numpy.linalg import *
import sys
import random

def beginToLearn(candidates, scores):
    mat = array(candidates)
    yMat = array([scores]).transpose()
    tmp = inv(dot(mat.transpose(),mat))
    tmp = dot(tmp,mat.transpose())
    result = dot(tmp,yMat)
    return result.transpose()


def getBestLamda(candidates,scores):

    leastCost = 999999999
    bestLamdaHere = 0.5
    newValue = None

    for lam in range(1,10000,10):
        lam = lam/100000.0
        cost = 0.0
        for i in range(len(candidates)):
            cand = candidates[i]
            score = scores[i]
            newCandidates = candidates[:]
            newScores = scores[:]
            newCandidates.remove(cand)
            newScores.remove(score)
            cost += doRegressionValidate(newCandidates,newScores,lam,cand,score)
        if cost < leastCost:
            leastCost = cost
            bestlamdaHere = lam
            newValue = lam

    return newValue
            


def doRegressionValidate(candidates,scores,lamda,cand,score):
    mat = array(candidates)
    xdim,ydim = mat.shape
    iMat = eye(ydim)
    yMat = array([scores]).transpose()
    tmp = dot(inv(dot(mat.transpose(),mat)+lamda*iMat),mat.transpose())
    tmp = dot(tmp,yMat)
    candResult = sum(dot(array([cand]),tmp))
    candResult = candResult-score
    candResult = candResult*candResult
    return candResult


def ridgeRegress(candidates,scores,lamdaHere):

    mat = array(candidates)
    xdim,ydim = mat.shape
    iMat = eye(ydim)
    yMat = array([scores]).transpose()
    tmp = dot(inv(dot(mat.transpose(),mat)+lamdaHere*iMat),mat.transpose())
    tmp = dot(tmp, yMat)

    return tmp.transpose()

def makeDecison(weights,candidates,scores,flag,attrNum):
    tmp = weights[0]
    decision = list(tmp)
    if flag %2 == 0:
        decision = generateCandidate(candidates,attrNum)
        #for i in range(len(decision)):
         #   decision[i] = round(decision[i],4)
          #  if decision[i] <0:
           #     decision[i] = -decision[i]
            #else:
             #   decision[i] = decision[i]
    else:
        for i in range(len(decision)):
            if decision[i] > 0:
                decision[i] = 1
            else:
                decision[i] = 0

    print decision
    return decision

def matrixrank(A,tol=1e-8):
    s = svd (A,compute_uv = 0)
    return sum(where(s>tol,1,0))

def generateCandidate(candidates,attrNum):
    
    newCond = []
    count = 0
    while True:
        current = candidates[:]
        old = candidates[:]
        del newCond[:]
        newCond[:] =[]

        for i in range(attrNum):
            newCond.append(round(random.random(),4))
        
        mat = array(old)
        current.append(newCond)
        mat2 = array(current)
        preRank = matrixrank(mat)
        currentRank = matrixrank(mat2)
        if currentRank > preRank or count >=10:
            break
        count += 1

    return newCond


if __name__ == '__main__':
    
    if len(sys.argv)!=4:
        print "Pleae specify host port N"
        sys.exit(1)

    host = sys.argv[1]
    port = int(sys.argv[2])
    attrNum = int(sys.argv[3])
    bufferSize = 10241024
    ADDR = (host,port)

    init = False;
    candidates = []
    first = []
    for i in range(attrNum):
        first.append(1)

    candidates.append(first)
        
    scores = [0]
    weights = []
    flag = 1
    bestLamda = None

    client = socket(AF_INET, SOCK_STREAM)
    client.connect(ADDR)

    while True:
        data = client.recv(bufferSize)
           
        if data.find("Bye") != -1:
            break
        if init == False:
            init = True
            data = data.splitlines()
            data = data[1:-1]
            for cond in data:
                index = cond.find(':')
                tmp = cond[0:index]
                tmp = eval(tmp)
                candidates.append(tmp)
                tmp = cond[index:]
                index1 = tmp.find("[")
                index2 = tmp.find("]")
                score = tmp[index1+1:index2]
                score = eval(score)
                scores.append(score)
            #weights = beginToLearn(candidates,scores)
            bestLamda = getBestLamda(candidates,scores)
            #print bestLamda
            weights = ridgeRegress(candidates,scores,bestLamda)
        else:
            data = data.splitlines()
            data = data[0:-1]
           # print data
            for cond in data:
                index = cond.find(":")
                tmp = cond[0:index]
                tmp = eval(tmp)
                candidates.append(tmp)
                tmp = cond[index:]
                index1 = tmp.find("[")
                index2 = tmp.find("]")
                score = tmp[index1+1:index2]
                score = eval(score)
                scores.append(score);
            #print len(candidates)
            weights = ridgeRegress(candidates,scores,bestLamda)
            #weights = beginToLearn(candidates,scores)

        decision = makeDecison(weights,candidates,scores,flag,attrNum)
        flag += 1
        print decision
        client.send(str(decision))

    client.close()

