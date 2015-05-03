package com.chickenkiller.unit8.ir4rg.domaci1.zadatakc;

import java.io.*;
import java.util.HashMap;

import javax.sound.sampled.*;

public class SoundManager {
	private static final String SOUNDS_CLASS_PATH = "/sounds/";
	
    private static SoundManager instance = null;
    
    private final HashMap<String, Clip> soundFiles;
    
    private SoundManager() {
    	this.soundFiles = new HashMap<String, Clip>();
    }
    
    public static SoundManager getInstance() {
    	if (SoundManager.instance == null) {
    		SoundManager.instance = new SoundManager();
    	}
        return SoundManager.instance;
    }
   
    /**
     * Loads all passed audio resources, and adds them to the soundFiles hash
     * @param soundsClassPath Class path to the root of all sound files
     * @param fileNames Array of file names relative to SOUNDS_CLASS_PATH
     */
    public void loadSoundFx(final String[] fileNames) {
        this.soundFiles.clear();
        for (String fileName: fileNames) {
            try {
	            String path = SoundManager.SOUNDS_CLASS_PATH + fileName;
	        	// First load the sound resource into an AudioInputStream object
	        	InputStream is = Game.class.getResourceAsStream(path);
	        	BufferedInputStream bis = new BufferedInputStream(is);
            	AudioInputStream audioInputStream;
				audioInputStream = AudioSystem.getAudioInputStream(bis);
                if (audioInputStream == null) {
                	continue;
                }
                AudioFormat format = audioInputStream.getFormat();
		    	DataLine.Info info = new DataLine.Info(Clip.class, format);
				// Create a clip object and use it to open the stream
				Clip clip = (Clip) AudioSystem.getLine(info);
				clip.open(audioInputStream);
				// Add the clip to the hash map to be played later
				this.soundFiles.put(fileName, clip);
            } catch (IOException e) {
				e.printStackTrace();
			} catch (UnsupportedAudioFileException e) {
				e.printStackTrace();
			} catch (LineUnavailableException e) {
				e.printStackTrace();
			}
        }
    }
   
	/**
	 * Plays the sound that corresponds to the passed filename
	 * @param fileName
	 */
    public void play(final String fileName) {
    	if (GameState.getInstance().isMuted()) {
    		return;
    	}
        Clip clip = this.soundFiles.get(fileName);
        if (clip == null) {
        	return;
        }
        if (clip.isRunning()) {
        	clip.stop();
        }
        clip.setFramePosition(0);
        clip.start();
    }
}
