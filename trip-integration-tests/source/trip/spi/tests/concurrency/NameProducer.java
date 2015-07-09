package trip.spi.tests.concurrency;

import java.util.ArrayList;
import java.util.List;

import lombok.val;
import trip.spi.Producer;
import trip.spi.Singleton;

@Singleton
public class NameProducer {

	@Producer( name = "names" )
	public List<String> produceNames() {
		val list = new ArrayList<String>();
		list.add( "Ereim" );
		list.add( "Leinil" );
		list.add( "Nedleh" );
		list.add( "Annailop" );
		return list;
	}
}
