package com.gitlab.jeeto.oboco.api.v1.bookscanner;

import com.gitlab.jeeto.oboco.common.exception.ProblemException;

public interface BookScannerService {
	public String getId();
	public BookScannerServiceStatus getStatus();
	public void start() throws ProblemException;
	public void stop() throws ProblemException;
}
