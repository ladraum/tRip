package trip.spi;

@Singleton( name = "foo" )
public class PrintableFoo implements PrintableWord {

	@Override
	public String getWord() {
		return null;
	}
}
