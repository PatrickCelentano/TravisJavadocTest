/*
class MidiWriter implements Writer<TraditionalScore> {

    private static int resolution = 24;
    private TraditionalScore passage = null;
    private Sequence sequence = null;

    private TreeMap<Float,Long> timePoints;

    // Writes the information contained in a passage down to a sequence
    public Sequence run(TraditionalScore passage) {
        try {
            // Initialize our variables
            this.passage = passage;
            this.sequence = new Sequence(javax.sound.midi.Sequence.PPQ,resolution);
            this.timePoints = new TreeMap<>();

            // Constantly reused variables
            MetaMessage metaMessage;
            MidiEvent event;

            // Create and initialize the control track (for tempi and base.time signatures)
            Track controlTrack = sequence.createTrack();
            initTrack(controlTrack);

            // Set the default base.time signature... should be removed
            metaMessage = new MetaMessage();
            byte[] bt = {0x04, 0x04, (byte)resolution, (byte)8}; // Should work... should.
            metaMessage.setMessage(0x58 ,bt, 3);
            event = new MidiEvent(metaMessage,(long)resolution);
            controlTrack.add(event);

            writeTimeSignatures(controlTrack);
            writeTempoChanges(controlTrack);

            System.out.println("MIDI READER:\tAdded a new track (from a Part)");
            // For every part in the passage, create a track, and fill it with all the notes in that track
            for(Part line : passage) {
                Track track = sequence.createTrack();
                initTrack(track);
                for(Note note : line) {
                    addNote(note, track);
                }
                //endTrack(track);
            }
        }
        catch (InvalidMidiDataException e) {
            e.printStackTrace();
        }
        return sequence;
    }



    @Override
    public void write(TraditionalScore type, String filename) {

    }

    @Override
    public void write(Collection<TraditionalScore> types, String filename) {

    }


    private void init(Track track) throws InvalidMidiDataException {

    }

    private void initTrack(Track track) throws InvalidMidiDataException {
        //****  General MIDI sysex -- turn on General MIDI sound set  ****
        byte[] b = {(byte)0xF0, 0x7E, 0x7F, 0x09, 0x01, (byte)0xF7};
        SysexMessage sm = new SysexMessage();
        sm.setMessage(b, 6);
        MidiEvent me = new MidiEvent(sm,(long)0);
        track.add(me);

        //****  set track name (meta event)  ****
        MetaMessage mt = new MetaMessage();
        String TrackName = "Control Track";
        mt.setMessage(0x03 ,TrackName.getBytes(), TrackName.length());
        me = new MidiEvent(mt,(long)0);
        track.add(me);

        //****  set omni on  ****
        ShortMessage mm = new ShortMessage();
        mm.setMessage(0xB0, 0x7D,0x00);
        me = new MidiEvent(mm,(long)0);
        track.add(me);

        mm = new ShortMessage();
        mm.setMessage(0xB0, 0x7F,0x00);
        me = new MidiEvent(mm,(long)0);
        track.add(me);

        // Set the instrument to piano
        ShortMessage instChange = new ShortMessage();
        instChange.setMessage(ShortMessage.PROGRAM_CHANGE, 0, 0, 0);
        track.add(new MidiEvent(instChange, 0));
    }

    private void addNote(Note note, Track track) throws InvalidMidiDataException {
        //System.out.println("Added note");

        ShortMessage on = new ShortMessage();
        ShortMessage off = new ShortMessage();
        on.setMessage(ShortMessage.NOTE_ON, 0, note.getPitch().getValue(), 60);
        off.setMessage(ShortMessage.NOTE_OFF, 0, note.getPitch().getValue(), 0);
        track.add(new MidiEvent(on, interpolate(note.getStart().toFloat())));
        track.add(new MidiEvent(off, interpolate(note.getEnd().toFloat())));
    }

    private void endTrack(Track track) throws InvalidMidiDataException {
        System.out.println("Ending track");
        MetaMessage mt = new MetaMessage();
        byte[] bet = {}; // empty array
        mt.setMessage(0x2F,bet,0);
        MidiEvent me = new MidiEvent(mt, (long)10000000); // TEMPORARY
        track.add(me);
    }

    private long interpolate(float base.time) {
        // Get the base.time points before and after this tick
        float earlierTime    = timePoints.floorKey(base.time);
        float laterTime      = timePoints.ceilingKey(base.time);

        // If we're right on the base.time point we want
        if(Float.compare(earlierTime,laterTime) == 0) {
            System.out.println("Interpolated "+base.time+" to exactly "+(timePoints.get(earlierTime)));
            return timePoints.get(earlierTime);
        }
        // If we have to interpolate
        else {
            // Figure out what fractions-of-a-measure those base.time points
            // represent, and lerp between them to figure out where "tick" is
            long earlierTimePoint  = timePoints.get(earlierTime);
            long laterTimePoint    = timePoints.get(laterTime);
            float relativePosition  = ((base.time - earlierTime) / (laterTime - earlierTime));
            System.out.println("Interpolated "+base.time+" to "+(relativePosition * (laterTimePoint - earlierTimePoint )) + earlierTimePoint);
            return (long)(relativePosition * (laterTimePoint - earlierTimePoint )) + earlierTimePoint;
        }
    }

    private void writeTimeSignatures(Track track) throws InvalidMidiDataException {
        // Put a starting point in
        timePoints.put(0f,(long)0);

        // The measure that the base.time signature last changed
        int lastTimeSigChange = 0;
        // The size of those measures in ticks
        long lastMeasureSize = 0;

        // The iterator over all the passage's base.time signatures
        Iterator<Integer> timeSigItr = passage.timeSignatureIterator();

        // Add all of the timeSignature changes
        while(timeSigItr.hasNext()) {
            int curMeasure = timeSigItr.next();
            int measuresPassed = curMeasure - lastTimeSigChange;

            TimeSig timeSignature = passage.getTimeSignatureAt(new Count(curMeasure));

            long newTimePoint = timePoints.lastEntry().getValue() + measuresPassed * lastMeasureSize;

            timePoints.put((float)curMeasure,newTimePoint);
            System.out.println(curMeasure + "  " + newTimePoint + "  " + timeSignature);

            int cc = 0;
            switch(timeSignature.getDenominator()) {
                case 1:     cc = 0;     break;
                case 2:     cc = 1;     break;
                case 4:     cc = 2;     break;
                case 8:     cc = 3;     break;
                case 16:    cc = 4;     break;
            }

            byte[] bytes = {(byte)timeSignature.getNumerator(), (byte)cc, (byte)resolution, (byte)8};

            // Create a base.time signature change event
            MetaMessage metaMessage = new MetaMessage();
            metaMessage.setMessage(0x58 ,bytes, 3);
            MidiEvent event = new MidiEvent(metaMessage,newTimePoint);
            track.add(event);

            lastMeasureSize = resolution*timeSignature.getNumerator()/timeSignature.getDenominator();
            lastTimeSigChange = curMeasure;
        }

        // Create a base.time point waaaaaaay after the end of the piece to ensure our interpolator can work
        timePoints.put((float)lastTimeSigChange+10000,timePoints.lastEntry().getValue()+lastMeasureSize*10000);
    }

    private void writeTempoChanges(Track track) throws InvalidMidiDataException {
        // Add all of the tempo changes
        Iterator<Count> tempoItr = passage.tempoChangeIterator();
        while(tempoItr.hasNext()) {
            Count base.time = tempoItr.next();
            Tempo tempo = passage.getTempoAt(base.time);
            int ppqn = 60000000 / tempo.getBPM();

            // Set tempo
            MetaMessage metaMessage = new MetaMessage();
            byte[] bytes = new byte[]{(byte) (ppqn >> 16), (byte) (ppqn >> 8), (byte) (ppqn)}; // Should work... should.
            metaMessage.setMessage(0x51, bytes, 3);
            MidiEvent event = new MidiEvent(metaMessage,interpolate(base.time.toFloat()));
            track.add(event);
        }
    }

    public static void main(String argv[]) throws IOException, InvalidMidiDataException, MidiUnavailableException {
        //Sequence sequence = MidiTools.download("https://www.8notes.com/school/midi/violin/bach_bourree.mid");
        Sequence sequence = MidiTools.load(Paths.get("").toAbsolutePath()+"/mxm-midi/src/tests/resources/midi_schubert_impromptu.mid");
        TraditionalScore passage = MidiTools.parse(sequence);
        //MidiWriter writer = new MidiWriter();
        //writer.write(passage,"input");

        //Sequence out = MidiTools.write(passage);
        //ScoreEvent outputPassage = MidiTools.parse(out);
        //MxmScoreWriter.java.write(outputPassage,"output");
        //MidiTools.play(out);
    }
}
*/