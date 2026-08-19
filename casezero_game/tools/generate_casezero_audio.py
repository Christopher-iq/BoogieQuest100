#!/usr/bin/env python3
import math, os, random, struct, wave
from pathlib import Path

OUT = Path('casezero/src/main/res/raw')
OUT.mkdir(parents=True, exist_ok=True)
SR = 44100
random.seed(1908)

def clamp(x):
    return max(-1.0, min(1.0, x))

def write_stereo(name, seconds, fn):
    path = OUT / name
    with wave.open(str(path), 'wb') as w:
        w.setnchannels(2); w.setsampwidth(2); w.setframerate(SR)
        frames = bytearray()
        total = int(seconds * SR)
        for i in range(total):
            t = i / SR
            l, r = fn(t, i)
            frames += struct.pack('<hh', int(clamp(l)*32767), int(clamp(r)*32767))
            if len(frames) >= 262144:
                w.writeframesraw(frames); frames.clear()
        if frames: w.writeframesraw(frames)
    print(path, path.stat().st_size)

def write_mono(name, seconds, fn):
    path = OUT / name
    with wave.open(str(path), 'wb') as w:
        w.setnchannels(1); w.setsampwidth(2); w.setframerate(SR)
        frames = bytearray()
        for i in range(int(seconds*SR)):
            t=i/SR; v=clamp(fn(t,i)); frames += struct.pack('<h', int(v*32767))
        w.writeframes(frames)
    print(path, path.stat().st_size)

noise_l = 0.0
noise_r = 0.0

def noir(t, i):
    global noise_l, noise_r
    noise_l = noise_l * 0.965 + (random.random()*2-1) * 0.035
    noise_r = noise_r * 0.963 + (random.random()*2-1) * 0.037
    rain_l = noise_l * 0.14
    rain_r = noise_r * 0.14
    breath = 0.58 + 0.16*math.sin(2*math.pi*0.043*t)
    l = 0.050*math.sin(2*math.pi*110.0*t) + 0.032*math.sin(2*math.pi*130.81*t+0.7) + 0.021*math.sin(2*math.pi*164.81*t+1.8)
    r = 0.047*math.sin(2*math.pi*109.7*t+0.14) + 0.034*math.sin(2*math.pi*131.1*t+0.9) + 0.020*math.sin(2*math.pi*164.55*t+2.0)
    cyc = t % 13.0
    thunder = math.exp(-((cyc-2.0)/1.8)**2) * math.sin(2*math.pi*43*t) * 0.045
    return (rain_l + l*breath + thunder, rain_r + r*breath + thunder*0.94)

def tension_fn(t, i):
    pulse = max(0.0, math.sin(2*math.pi*0.92*t))**10
    bass = math.sin(2*math.pi*55*t)*0.075 + math.sin(2*math.pi*82.41*t+0.3)*0.025
    high = math.sin(2*math.pi*220*t + 0.35*math.sin(2*math.pi*.13*t))*0.012
    click = pulse * math.exp(-((t*4)%1)*9) * 0.07
    return (bass+high+click, bass*0.96-high+click*0.82)

write_stereo('noir_ambient.wav', 48.0, noir)
write_stereo('tension_loop.wav', 34.0, tension_fn)
write_mono('ui_click.wav', .09, lambda t,i: math.sin(2*math.pi*(980-500*t/.09)*t)*math.exp(-t*42)*0.28)
write_mono('evidence_sting.wav', .75, lambda t,i: (math.sin(2*math.pi*440*t)*.12 + math.sin(2*math.pi*660*t)*.09 + math.sin(2*math.pi*880*t)*.05)*math.exp(-t*3.8))
write_mono('case_solved.wav', 1.8, lambda t,i: (math.sin(2*math.pi*220*t)*.09 + math.sin(2*math.pi*277.18*t)*.08 + math.sin(2*math.pi*329.63*t)*.07 + math.sin(2*math.pi*440*t)*.05)*min(1,t*3)*math.exp(-t*.7))
write_mono('soft_fail.wav', .55, lambda t,i: math.sin(2*math.pi*(180-70*t/.55)*t)*math.exp(-t*5)*0.16)
