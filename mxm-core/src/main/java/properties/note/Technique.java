package properties.note;

import properties.AbstractStringProp;

/**
 * <p> <b>Class overview:</b>
 * ASDF</p>
 *
 * <p> <b>Design Details:</b>
 * ASDF</p>
 *
 * TODO: this comment
 * Techniques encompass any style of playing on an instrument that modifies the tone/style of any Pitch an instrument
 * can play. For instance, muting is a common technique that changes the timbre of a violin, trumpet, or clarinet's tone
 * without actually modifying the sound being played.
 *
 * In the case of non-pitched instruments like a drum set, Techniques do not involve different Pitches or even drums.
 * Instead, Techniques represent different common ways of producing those Sounds- single hits vs. rolls, for instance,
 * can be performed on a drum set's toms, snare, cymbals, etc. There are of course some limitations to this, as one
 * cannot roll on a bass drum, but the general principle holds.
 *
 * It is perhaps useful to think of Techniques as accents of sorts, applicable to any note an instrument can produce to
 * modify its sound.
 *
 * @author Patrick Celentano
 */
public class Technique extends AbstractStringProp {
    public static Technique DEFAULT;
    public static Technique PIZZICATO;
    public static Technique STOP_MUTE;
    public static Technique HARMON_MUTE;

    public static Technique HIT;
    public static Technique ROLL;

    /**
     * A constructor for a string-based property
     *
     * @param value the value of this property
     */
    protected Technique(String value) {
        super(value);
    }
}
