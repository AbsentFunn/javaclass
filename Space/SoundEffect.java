package Space;

import javax.sound.sampled.*;

/**
 * Programmatic chiptune sound effect generator.
 * Optimized for smoother "background" levels and softer attacks.
 */
public class SoundEffect {
    private static final int SAMPLE_RATE = 22050;
    private static final double MASTER_VOLUME = 0.2; // 20% volume for background feel
    private static final double ATTACK_TIME = 0.01; // 10ms fade-in to soften start

    public static void playFire() {
        play(generateSweep(400, 100, 0.1, WaveType.SQUARE));
    }

    public static void playExplosion() {
        play(generateNoise(0.15));
    }

    public static void playPlayerHit() {
        play(generateSweep(150, 50, 0.3, WaveType.SAWTOOTH));
    }

    public static void playLevelUp() {
        new Thread(() -> {
            play(generateTone(440, 0.1, WaveType.SINE));
            play(generateTone(554, 0.1, WaveType.SINE));
            play(generateTone(659, 0.2, WaveType.SINE));
        }).start();
    }

    private enum WaveType { SINE, SQUARE, SAWTOOTH }

    private static byte[] applyEnvelope(byte[] buf) {
        int samples = buf.length;
        int attackSamples = (int) (ATTACK_TIME * SAMPLE_RATE);
        
        for (int i = 0; i < samples; i++) {
            double envelope = 1.0;
            
            // Attack (Fade-in)
            if (i < attackSamples) {
                envelope = (double) i / attackSamples;
            } 
            // Decay (Fade-out)
            else {
                envelope = 1.0 - ((double) (i - attackSamples) / (samples - attackSamples));
            }
            
            double value = (buf[i] / 128.0) * envelope * MASTER_VOLUME;
            buf[i] = (byte) (value * 127);
        }
        return buf;
    }

    private static byte[] generateTone(double freq, double duration, WaveType type) {
        int samples = (int) (duration * SAMPLE_RATE);
        byte[] buf = new byte[samples];
        for (int i = 0; i < samples; i++) {
            double t = (double) i / SAMPLE_RATE;
            double angle = 2.0 * Math.PI * freq * t;
            double value = 0;
            switch (type) {
                case SINE: value = Math.sin(angle); break;
                case SQUARE: value = Math.sin(angle) > 0 ? 1 : -1; break;
                case SAWTOOTH: value = 2.0 * (t * freq - Math.floor(0.5 + t * freq)); break;
            }
            buf[i] = (byte) (value * 127);
        }
        return applyEnvelope(buf);
    }

    private static byte[] generateSweep(double startFreq, double endFreq, double duration, WaveType type) {
        int samples = (int) (duration * SAMPLE_RATE);
        byte[] buf = new byte[samples];
        for (int i = 0; i < samples; i++) {
            double progress = (double) i / samples;
            double freq = startFreq + (endFreq - startFreq) * progress;
            double t = (double) i / SAMPLE_RATE;
            double value = 0;
            double angle = 2.0 * Math.PI * freq * t; 
            switch (type) {
                case SINE: value = Math.sin(angle); break;
                case SQUARE: value = Math.sin(angle) > 0 ? 1 : -1; break;
                case SAWTOOTH: value = 2.0 * (t * freq - Math.floor(0.5 + t * freq)); break;
            }
            buf[i] = (byte) (value * 127);
        }
        return applyEnvelope(buf);
    }

    private static byte[] generateNoise(double duration) {
        int samples = (int) (duration * SAMPLE_RATE);
        byte[] buf = new byte[samples];
        java.util.Random rand = new java.util.Random();
        for (int i = 0; i < samples; i++) {
            buf[i] = (byte) (rand.nextInt(256) - 128);
        }
        return applyEnvelope(buf);
    }

    private static void play(byte[] buf) {
        new Thread(() -> {
            try {
                AudioFormat af = new AudioFormat(SAMPLE_RATE, 8, 1, true, false);
                SourceDataLine sdl = AudioSystem.getSourceDataLine(af);
                sdl.open(af);
                sdl.start();
                sdl.write(buf, 0, buf.length);
                sdl.drain();
                sdl.close();
            } catch (Exception e) {
                // Silently fail if audio device is busy
            }
        }).start();
    }
}
