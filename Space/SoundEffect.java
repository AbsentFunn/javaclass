package Space;

import javax.sound.sampled.*;
import java.io.ByteArrayInputStream;

/**
 * Zero-latency sound effect engine.
 * Pre-generates all audio and uses a pool of pre-opened clips.
 */
public class SoundEffect {
    private static final int SAMPLE_RATE = 22050;
    private static final int POOL_SIZE = 5; // Allow 5 instances of each sound to overlap

    private static Clip[] firePool = new Clip[POOL_SIZE];
    private static Clip[] explosionPool = new Clip[POOL_SIZE];
    private static Clip[] hitPool = new Clip[POOL_SIZE];
    private static Clip[] levelPool = new Clip[1];
    private static Clip[] powerupPool = new Clip[1];

    private static int firePtr = 0;
    private static int explosionPtr = 0;
    private static int hitPtr = 0;

    static {
        try {
            AudioFormat af = new AudioFormat(SAMPLE_RATE, 8, 1, true, false);
            
            // Pre-generate buffers
            byte[] fireBuf = generateSweep(120, 60, 0.12);
            byte[] explosionBuf = generateDeepRumble(0.25);
            byte[] hitBuf = generateSweep(80, 30, 0.4);
            byte[] levelBuf = generateLevelMelody();
            byte[] powerupBuf = generatePowerupMelody();

            // Initialize pools
            for (int i = 0; i < POOL_SIZE; i++) {
                firePool[i] = createClip(af, fireBuf);
                explosionPool[i] = createClip(af, explosionBuf);
                hitPool[i] = createClip(af, hitBuf);
            }
            levelPool[0] = createClip(af, levelBuf);
            powerupPool[0] = createClip(af, powerupBuf);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void playFire() { playFromPool(firePool, firePtr++ % POOL_SIZE); }
    public static void playExplosion() { playFromPool(explosionPool, explosionPtr++ % POOL_SIZE); }
    public static void playPlayerHit() { playFromPool(hitPool, hitPtr++ % POOL_SIZE); }
    public static void playLevelUp() { playFromPool(levelPool, 0); }
    public static void playPowerup() { playFromPool(powerupPool, 0); }

    private static void playFromPool(Clip[] pool, int idx) {
        Clip c = pool[idx];
        if (c == null) return;
        if (c.isRunning()) c.stop();
        c.setFramePosition(0);
        c.start();
    }

    private static Clip createClip(AudioFormat af, byte[] buf) throws Exception {
        Clip clip = AudioSystem.getClip();
        clip.open(af, buf, 0, buf.length);
        return clip;
    }

    // --- Audio Generation Logic ---

    private static byte[] generateSweep(double startFreq, double endFreq, double duration) {
        int samples = (int) (duration * SAMPLE_RATE);
        byte[] buf = new byte[samples];
        for (int i = 0; i < samples; i++) {
            double progress = (double) i / samples;
            double freq = startFreq + (endFreq - startFreq) * progress;
            double t = (double) i / SAMPLE_RATE;
            double value = Math.sin(2.0 * Math.PI * freq * t);
            buf[i] = (byte) (value * 127);
        }
        return applyEnvelope(buf, 0.25); // Lowered from 0.4
    }

    private static byte[] generateDeepRumble(double duration) {
        int samples = (int) (duration * SAMPLE_RATE);
        byte[] buf = new byte[samples];
        java.util.Random rand = new java.util.Random();
        double lastValue = 0;
        for (int i = 0; i < samples; i++) {
            double white = rand.nextDouble() * 2.0 - 1.0;
            // Softer low-pass for a less "crunchy" sound
            double smoothed = lastValue + 0.05 * (white - lastValue);
            lastValue = smoothed;
            buf[i] = (byte) (smoothed * 127);
        }
        return applyEnvelope(buf, 0.15); // Lowered from 0.5
    }

    private static byte[] generateLevelMelody() {
        // Concat three tones
        byte[] t1 = generateTone(220, 0.15);
        byte[] t2 = generateTone(277, 0.15);
        byte[] t3 = generateTone(329, 0.3);
        byte[] combined = new byte[t1.length + t2.length + t3.length];
        System.arraycopy(t1, 0, combined, 0, t1.length);
        System.arraycopy(t2, 0, combined, t1.length, t2.length);
        System.arraycopy(t3, 0, combined, t1.length + t2.length, t3.length);
        return combined;
    }

    private static byte[] generatePowerupMelody() {
        byte[] t1 = generateTone(440, 0.1);
        byte[] t2 = generateTone(880, 0.1);
        byte[] combined = new byte[t1.length + t2.length];
        System.arraycopy(t1, 0, combined, 0, t1.length);
        System.arraycopy(t2, 0, combined, t1.length, t2.length);
        return combined;
    }

    private static byte[] generateTone(double freq, double duration) {
        int samples = (int) (duration * SAMPLE_RATE);
        byte[] buf = new byte[samples];
        for (int i = 0; i < samples; i++) {
            double t = (double) i / SAMPLE_RATE;
            buf[i] = (byte) (Math.sin(2.0 * Math.PI * freq * t) * 127);
        }
        return applyEnvelope(buf, 0.2); // Lowered from 0.4
    }

    private static byte[] applyEnvelope(byte[] buf, double volume) {
        int samples = buf.length;
        int attackSamples = (int) (0.02 * SAMPLE_RATE); // 20ms attack
        for (int i = 0; i < samples; i++) {
            double env = i < attackSamples ? (double) i / attackSamples : 1.0 - (double)(i-attackSamples)/(samples-attackSamples);
            buf[i] = (byte) ((buf[i] / 128.0) * env * volume * 127);
        }
        return buf;
    }
}
