#!/usr/bin/env python
# -*- coding: utf-8 -*-

f = open("Dict.txt")
for line in f:
    line = line.lower()
    for i in range(6):
        line = line.replace(u"áéíóúü"[i], u"aeiouu"[i])
    line = "".join([ch for ch in line if ch in (u"abcdefghijklmnñopqrstuvwxyz")])
    print(line) 
