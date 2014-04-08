package trip.spi;

import trip.spi.Provided;

public class PrintableHello implements Printable {

	@Provided PrintableWord word;

	@Override
	public String toString() {
		return "Hello " + word.getWord();
	}
}
