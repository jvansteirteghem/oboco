package com.gitlab.jeeto.oboco.data.bookscanner;

import com.gitlab.jeeto.oboco.problem.ProblemException;

public interface BookScanner {
	public String getId();
	public BookScannerMode getMode();
	public BookScannerStatus getStatus();
	public void start(BookScannerMode mode) throws ProblemException;
	public void stop() throws ProblemException;
}
