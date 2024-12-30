import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;

public class SoundEffect {
    private static String bgmFile = "bgm.wav";
    private static String eatFile = "eat.wav";
    private static String deadFile = "dead.wav";
    private static String gameOverFile = "gameover.wav";
    private static String invincibleFile = "invincible.wav";
    private static String youwinFile = "youwin.wav";
    private static Clip bgmClip; //special clip for bgm as we need to control play/stop

    public static void main(String[] args) {
        SoundEffect sfx = new SoundEffect();
        //sfx.playSound(bgmFile);

        sfx.initBgm();
        // Open the audio stream and start playing
        sfx.startBgm();
        // Keep the program running to hear the audio
        System.out.println("Press Enter to stop...");
        try {
            System.in.read();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void initBgm(){
        //String resourcePath = getClass().getClassLoader().getResource(bgmFile).getPath();
        //String resourcePath = getClass().getClassLoader().getResource(deadFile).getPath();
        ClassLoader classLoader = getClass().getClassLoader();
        String resourcePath = classLoader.getResource(bgmFile).getPath();

        try {
            //load the audio file
            File audioFile = new File(resourcePath);
            System.out.print(audioFile.getAbsolutePath());

            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(audioFile);

            if(bgmClip == null){
                bgmClip = AudioSystem.getClip();
            }
            bgmClip.open(audioInputStream);
            bgmClip.loop(Clip.LOOP_CONTINUOUSLY);

            //bgmClip.start(); // Start playing the sound
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }


    public void startBgm(){
        if (bgmClip.getFramePosition() == bgmClip.getFrameLength()) {
            bgmClip.setFramePosition(0);
        }
        bgmClip.loop(Clip.LOOP_CONTINUOUSLY); //need to call this again as stop() would kill the previously set loop
        bgmClip.start();
    }

    public void stopBgm(){
        bgmClip.stop();
    }

    public void playEatSound(){
        playSound(eatFile);
    }

    public void playDeadSound(){
        playSound(deadFile);
    }

    public void playGameOverSound(){
        playSound(gameOverFile);
    }

    public void playYouWinSound(){
        playSound(youwinFile);
    }

    public void playInvincibleSound(){
        playSound(invincibleFile);
    }

    public void playSound(String soundFilePath) {
        playSound(soundFilePath, false );
    }

    public void playSound(String soundFilePath, boolean isLoop) {
        new Thread(() -> realPlaySound(soundFilePath, isLoop)).start();
    }

    private void realPlaySound(String soundFilePath, boolean isLoop) {
        String resourcePath = getClass().getClassLoader().getResource(soundFilePath).getPath();
        System.out.println(resourcePath);
        //InputStream inputStream = getClass().getResourceAsStream(resourcePath);

        try {
            // Load the audio file
            File audioFile = new File(resourcePath);
            System.out.print(audioFile.getAbsolutePath());

            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(audioFile);
            Clip clip;
            clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            if(isLoop) {
                clip.loop(Clip.LOOP_CONTINUOUSLY);
            }
            clip.start(); //start playing sound
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }
}
