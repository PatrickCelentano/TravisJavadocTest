package properties.time;

import properties.AbstractFractionProp;
import properties.sound.Pitch;
import org.jetbrains.annotations.NotNull;

/**
 * <p> <b>Class Overview:</b>
 * Time signatures represent metrical (that is to say, measure-based) repetitive patterns of rhythm and organize such
 * measures. These are in many ways structurally similar to {@link Time}. The major difference is that there's no
 * reason to ever reduce a time signature from say, 4/4 to 2/2.
 *
 * <p> <b>Design Details:</b>
 * This class is <i>immutable</i> and implements a <b>factory design pattern</b>- users cannot construct new instances
 * of this class, but must call get() instead. This is mostly a stylistic choice.</p>
 *
 * @author Patrick Celentano
 */
public class TimeSig extends AbstractFractionProp {

    //////////////////////////////
    // Static variables         //
    //////////////////////////////

    public static final TimeSig CUT_TIME = get(2,2);
    public static final TimeSig COMMON_TIME = get(4,4);

    public static final TimeSig TWO_FOUR = get(2,4);
    public static final TimeSig THREE_FOUR = get(3,4);
    public static final TimeSig FOUR_FOUR = get(4,4);
    public static final TimeSig FIVE_FOUR = get(5,4);

    public static final TimeSig THREE_EIGHT = get(3,8);
    public static final TimeSig FIVE_EIGHT = get(5,8);
    public static final TimeSig SIX_EIGHT = get(6,8);
    public static final TimeSig SEVEN_EIGHT = get(7,8);
    public static final TimeSig NINE_EIGHT = get(9,8);
    public static final TimeSig ELEVEN_EIGHT = get(11,8);
    public static final TimeSig TWELVE_EIGHT = get(12,8);

    public static final TimeSig DEFAULT = COMMON_TIME;

    //////////////////////////////
    // Static methods           //
    //////////////////////////////

    /**
     * This get method dresses up the TimeSig constructor to match the other classes of the note package such as {@link
     * Pitch}, {@link Tempo}, and so forth.
     * @param num The desired numerator
     * @param den The desired denominator
     * @return a tempo of this speed (in beats per minute)
     */
    public static @NotNull TimeSig get(int num, int den) {
        // Ensure that the numerator is greater than zero
        if(num <= 0) throw new Error("Time Signature: The numerator must greater than 0!");
        // Ensure that the denominator is greater than zero
        if(den <= 0) throw new Error("Time Signature: The denominator must greater than 0!");
        // Do some bit-magic to check if the denominator is a power of two
        if(!((den & (den - 1)) == 0)) throw new Error("Time Signature: The denominator must be a power of two!");
        // Constructor
        return new TimeSig(num, den);
    }

    //////////////////////////////
    // Member methods           //
    //////////////////////////////

    /**
     * A constructor for time signature taking in a numerator and denominator.
     * @param num The desired numerator
     * @param den The desired denominator
     */
    private TimeSig(int num, int den) {
        super(num,den);
    }
    /**
     * Returns a basic string representation of this time signature
     * @return a String representation of this time signature
     */
    @Override
    public final @NotNull String toString() {
        return super.toString()+ " time";
    }
    /**
     * A noteQualities (generated) equals() method for note.time.TimeSig.
     * @param object The Object to compare this to.
     * @return If these two Objects are equal.
     */
    @Override
    public final boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        TimeSig timeSignature = (TimeSig) object;
        return denominator == timeSignature.denominator &&
                numerator == timeSignature.numerator;
    }
}
