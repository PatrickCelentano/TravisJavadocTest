package events;

import org.jetbrains.annotations.NotNull;
import form.part.AbstractPart;

public interface IPartEvent extends IMusicEvent {
    public @NotNull
    AbstractPart getPart();
}
