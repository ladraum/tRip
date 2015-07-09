package trip.spi.tests;

import trip.spi.Stateless;

@Stateless
public class PostConstructAndPreDestroyStatelessService {

	final Status status = new Status();

	@javax.annotation.PostConstruct
	public void postConstructorJava() {
		status.calledPostContructJavaAnnotation = true;
	}

	@trip.spi.PostConstruct
	public void postConstructorTrip() {
		status.calledPostContructTrip = true;
	}

	@javax.annotation.PreDestroy
	public void preDestroyJava() {
		status.calledPreDestroyJavaAnnotation = true;
	}

	@trip.spi.PreDestroy
	public void preDestroyTrip() {
		status.calledPreDestroyTrip = true;
	}

	public Status getStatus() {
		return status;
	}
}

class Status {

	boolean calledPreDestroyJavaAnnotation;
	boolean calledPreDestroyTrip;
	boolean calledPostContructJavaAnnotation;
	boolean calledPostContructTrip;
}