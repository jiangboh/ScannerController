/*
 * Copyright 2015 Yan Zhenjie
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.bravo.no_http.error;

/**
 * Created in 2016/4/17 22:40.
 *
 * @author Yan Zhenjie.
 */
public class ServerError extends Exception {

    private static final long serialVersionUID = 1854642L;

    private String errorBody;

    public ServerError() {
    }

    public ServerError(String detailMessage) {
        super(detailMessage);
    }

    public ServerError(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public ServerError(Throwable throwable) {
        super(throwable);
    }

    /**
     * To getList the wrong information.
     *
     * @return the error message.
     */
    public String getErrorBody() {
        return errorBody;
    }

    /**
     * To set the wrong information.
     *
     * @param errorBody the error message.
     */
    public void setErrorBody(String errorBody) {
        this.errorBody = errorBody;
    }
}
