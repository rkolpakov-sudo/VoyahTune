---
license: apache-2.0
language:
- ru
tags:
- audio
- automatic-speech-recognition
- hf-asr-leaderboard
- ru
- speech
model-index:
- name: Vosk Big Russian Model
  results:
  - task:
      name: Automatic Speech Recognition
      type: automatic-speech-recognition
    dataset:
      name: Common Voice ru
      type: common_voice
      args: ru
    metrics:
    - name: Test WER
      type: wer
      value: 6.1
---

Zipformer2 model trained with k2-fsa/icefall on Russian data

Version 0.54

Links:

<https://alphacephei.com/vosk>

<https://github.com/k2-fsa/icefall>
