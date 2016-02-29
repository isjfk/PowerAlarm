/**
 * (C) Copyright InfiniteSpace Studio, 2011-2011. All rights reserved.
 */
package com.isjfk.android.rac.common;

/**
 * RegularAlarmClock异常。
 *
 * @author Jimmy F. Klarke
 * @version 1.0, 2011-7-31
 */
public class RACException extends RuntimeException {

    private static final long serialVersionUID = -9115463498491507151L;

    public RACException() {
        super();
    }

    public RACException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public RACException(String detailMessage) {
        super(detailMessage);
    }

    public RACException(Throwable throwable) {
        super(throwable);
    }

}
