import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Sequencer;
import java.io.*;

public class PlayMidiSong {
    public static void main(String[] args) {
        // Obtains the default Sequencer connected to a default device.
        Sequencer sequencer = null;
        try {
            sequencer = MidiSystem.getSequencer();

            // Opens the device, indicating that it should now acquire any
            // system resources it requires and become operational.
            sequencer.open();

            // create a stream from a file
            InputStream is = new BufferedInputStream(PlayMidiSong.class.getResourceAsStream("/sounds/arizona.mid"));

            // Sets the current sequence on which the sequencer operates.
            // The stream must point to MIDI file data.
            sequencer.setSequence(is);

            // Starts playback of the MIDI data in the currently loaded sequence.
            sequencer.start();
        } catch (MidiUnavailableException | InvalidMidiDataException | IOException e) {
            throw new RuntimeException(e);
        }

    }
}