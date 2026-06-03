import struct
import math

def write_wav(filename, frequency, duration, volume=0.5, sample_rate=44100):
    num_samples = int(sample_rate * duration)
    data = []
    for i in range(num_samples):
        t = i / sample_rate
        fade = min(1.0, min(t * 20, (duration - t) * 20))
        sample = volume * fade * math.sin(2 * math.pi * frequency * t)
        data.append(int(sample * 32767))
    
    with open(filename, 'wb') as f:
        f.write(b'RIFF')
        f.write(struct.pack('<I', 36 + num_samples * 2))
        f.write(b'WAVE')
        f.write(b'fmt ')
        f.write(struct.pack('<I', 16))
        f.write(struct.pack('<H', 1))
        f.write(struct.pack('<H', 1))
        f.write(struct.pack('<I', sample_rate))
        f.write(struct.pack('<I', sample_rate * 2))
        f.write(struct.pack('<H', 2))
        f.write(struct.pack('<H', 16))
        f.write(b'data')
        f.write(struct.pack('<I', num_samples * 2))
        for s in data:
            f.write(struct.pack('<h', max(-32768, min(32767, s))))

def write_wav_chord(filename, freqs, duration, volume=0.4, sample_rate=44100):
    num_samples = int(sample_rate * duration)
    data = []
    for i in range(num_samples):
        t = i / sample_rate
        fade = min(1.0, min(t * 30, (duration - t) * 15))
        sample = 0
        for f in freqs:
            sample += volume * fade * math.sin(2 * math.pi * f * t)
        sample /= len(freqs)
        data.append(int(sample * 32767))
    
    with open(filename, 'wb') as f:
        f.write(b'RIFF')
        f.write(struct.pack('<I', 36 + num_samples * 2))
        f.write(b'WAVE')
        f.write(b'fmt ')
        f.write(struct.pack('<I', 16))
        f.write(struct.pack('<H', 1))
        f.write(struct.pack('<H', 1))
        f.write(struct.pack('<I', sample_rate))
        f.write(struct.pack('<I', sample_rate * 2))
        f.write(struct.pack('<H', 2))
        f.write(struct.pack('<H', 16))
        f.write(b'data')
        f.write(struct.pack('<I', num_samples * 2))
        for s in data:
            f.write(struct.pack('<h', max(-32768, min(32767, s))))

# NFC start - короткий beep
write_wav('app/src/main/res/raw/nfc_start.wav', 880, 0.15, 0.3)

# Success - приятный ding (аккорд)
write_wav_chord('app/src/main/res/raw/payment_success.wav',
    [523, 659, 784], 0.6, 0.4)

# Error - низкий buzz
write_wav_chord('app/src/main/res/raw/payment_error.wav',
    [220, 180], 0.4, 0.4)

print("Звуки созданы!")
