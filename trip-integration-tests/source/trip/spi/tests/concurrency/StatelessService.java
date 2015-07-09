package trip.spi.tests.concurrency;

import java.util.List;

import lombok.val;
import trip.spi.Provided;
import trip.spi.Stateless;

@Stateless
public class StatelessService {

	@Provided( name = "names" )
	List<String> names;

	@Provided
	Printer printer;

	void printNames() {
		val builder = new StringBuilder();
		for ( val name : names )
			builder.append( name ).append( ' ' );
		printer.print( builder.toString() );
	}
}
