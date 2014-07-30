package trip.spi;

public class PrintableWorld implements PrintableWord {

	@Provided( name = "period" )
	Closure closure;

	@Override
	public String getWord() {
		return "World" + this.closure.getSentenceClosureChar();
	}
}
