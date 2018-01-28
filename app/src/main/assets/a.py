#!/usr/bin/env python
# -*- coding: utf-8 -*-

f = open("Dict.txt")
for line in f:
    for i in range(6):
        line = line.replace("áéíóúü"[i], "aeiouu"[i])
    line = line.lower()
    line = "".join([ch for ch in line if ch in (u"abcdefghijklmnñopqrstuvwxyz")])
    print(line) 
