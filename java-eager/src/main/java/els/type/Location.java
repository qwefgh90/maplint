package els.type;

import java.net.URI;

public class Location {
    public URI uri;
    public Range range;

    public Location() {}

    public Location(URI uri, Range range) {
        this.uri = uri;
        this.range = range;
    }

    @Override
    public String toString() {
        return "Location{" +
                "uri=" + uri +
                ", range=" + range +
                '}';
    }

    public static final Location NONE = new Location(null, Range.NONE);
}
