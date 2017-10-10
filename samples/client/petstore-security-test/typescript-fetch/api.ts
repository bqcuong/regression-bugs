// tslint:disable
/**
 * Swagger Petstore *_/ ' \" =end -- \\r\\n \\n \\r
 * This spec is mainly for testing Petstore server and contains fake endpoints, models. Please do not use this for any other purpose. Special characters: \" \\  *_/ ' \" =end --       
 *
 * OpenAPI spec version: 1.0.0 *_/ ' \" =end -- \\r\\n \\n \\r
 * Contact: apiteam@swagger.io *_/ ' \" =end -- \\r\\n \\n \\r
 *
 * NOTE: This class is auto generated by the swagger code generator program.
 * https://github.com/swagger-api/swagger-codegen.git
 * Do not edit the class manually.
 */
 

import * as url from "url";
import * as isomorphicFetch from "isomorphic-fetch";
import { Configuration } from "./configuration";

const BASE_PATH = "https://petstore.swagger.io *_/ ' \" =end -- \\r\\n \\n \\r/v2 *_/ ' \" =end -- \\r\\n \\n \\r".replace(/\/+$/, "");

const COLLECTION_FORMATS = {
    csv: ",",
    ssv: " ",
    tsv: "\t",
    pipes: "|",
};

/**
 *
 * @export
 * @interface FetchAPI
 */
export interface FetchAPI {
    (url: string, init?: any): Promise<any>;
}

/**
 *  
 * @export
 * @interface FetchArgs
 */
export interface FetchArgs {
    url: string;
    options: any;
}

/**
 * 
 * @export
 * @class BaseAPI
 */
export class BaseAPI {
    protected configuration: Configuration;

    constructor(configuration?: Configuration, protected basePath: string = BASE_PATH, protected fetch: FetchAPI = isomorphicFetch) {
        if (configuration) {
            this.configuration = configuration;
            this.basePath = configuration.basePath || this.basePath;
        }
    }
};

/**
 * 
 * @export
 * @class RequiredError
 * @extends {Error}
 */
export class RequiredError extends Error {
    name: "RequiredError"
    constructor(public field: string, msg?: string) {
        super(msg);
    }
}

/**
 * Model for testing reserved words  *_/ ' \" =end -- \\r\\n \\n \\r
 * @export
 * @interface ModelReturn
 */
export interface ModelReturn {
    /**
     * property description  *_/ ' \" =end -- \\r\\n \\n \\r
     * @type {number}
     * @memberof ModelReturn
     */
    return?: number;
}


/**
 * FakeApi - fetch parameter creator
 * @export
 */
export const FakeApiFetchParamCreator = function (configuration?: Configuration) {
    return {
        /**
         * 
         * @summary To test code injection *_/ ' \" =end -- \\r\\n \\n \\r
         * @param {string} [test code inject * &#39; &quot; &#x3D;end  rn n r] To test code injection *_/ &#39; \&quot; &#x3D;end -- \\r\\n \\n \\r
         * @param {*} [options] Override http request option.
         * @throws {RequiredError}
         */
        testCodeInjectEndRnNR(test code inject * &#39; &quot; &#x3D;end  rn n r?: string, options: any = {}): FetchArgs {
            const path = `/fake`;
            const urlObj = url.parse(path, true);
            const requestOptions = Object.assign({ method: 'PUT' }, options);
            const headerParameter = {} as any;
            const queryParameter = {} as any;
            const formParams = new url.URLSearchParams();

            if (test code inject * &#39; &quot; &#x3D;end  rn n r !== undefined) {
                formParams.set('test code inject */ &#39; &quot; &#x3D;end -- \r\n \n \r', test code inject * &#39; &quot; &#x3D;end  rn n r as any);
            }

            headerParameter['Content-Type'] = 'application/x-www-form-urlencoded';

            urlObj.query = Object.assign({}, urlObj.query, queryParameter, options.query);
            requestOptions.headers = Object.assign({}, headerParameter, options.headers);
            requestOptions.body = formParams.toString();

            return {
                url: url.format(urlObj),
                options: requestOptions,
            };
        },
    }
};

/**
 * FakeApi - functional programming interface
 * @export
 */
export const FakeApiFp = function(configuration?: Configuration) {
    return {
        /**
         * 
         * @summary To test code injection *_/ ' \" =end -- \\r\\n \\n \\r
         * @param {string} [test code inject * &#39; &quot; &#x3D;end  rn n r] To test code injection *_/ &#39; \&quot; &#x3D;end -- \\r\\n \\n \\r
         * @param {*} [options] Override http request option.
         * @throws {RequiredError}
         */
        testCodeInjectEndRnNR(test code inject * &#39; &quot; &#x3D;end  rn n r?: string, options?: any): (fetch?: FetchAPI, basePath?: string) => Promise<any> {
            const fetchArgs = FakeApiFetchParamCreator(configuration).testCodeInjectEndRnNR(test code inject * &#39; &quot; &#x3D;end  rn n r, options);
            return (fetch: FetchAPI = isomorphicFetch, basePath: string = BASE_PATH) => {
                return fetch(basePath + fetchArgs.url, fetchArgs.options).then((response) => {
                    if (response.status >= 200 && response.status < 300) {
                        return response;
                    } else {
                        throw response;
                    }
                });
            };
        },
    }
};

/**
 * FakeApi - factory interface
 * @export
 */
export const FakeApiFactory = function (configuration?: Configuration, fetch?: FetchAPI, basePath?: string) {
    return {
        /**
         * 
         * @summary To test code injection *_/ ' \" =end -- \\r\\n \\n \\r
         * @param {string} [test code inject * &#39; &quot; &#x3D;end  rn n r] To test code injection *_/ &#39; \&quot; &#x3D;end -- \\r\\n \\n \\r
         * @param {*} [options] Override http request option.
         * @throws {RequiredError}
         */
        testCodeInjectEndRnNR(test code inject * &#39; &quot; &#x3D;end  rn n r?: string, options?: any) {
            return FakeApiFp(configuration).testCodeInjectEndRnNR(test code inject * &#39; &quot; &#x3D;end  rn n r, options)(fetch, basePath);
        },
    };
};

/**
 * FakeApi - object-oriented interface
 * @export
 * @class FakeApi
 * @extends {BaseAPI}
 */
export class FakeApi extends BaseAPI {
    /**
     * 
     * @summary To test code injection *_/ ' \" =end -- \\r\\n \\n \\r
     * @param {} [test code inject * &#39; &quot; &#x3D;end  rn n r] To test code injection *_/ &#39; \&quot; &#x3D;end -- \\r\\n \\n \\r
     * @param {*} [options] Override http request option.
     * @throws {RequiredError}
     * @memberof FakeApi
     */
    public testCodeInjectEndRnNR(test code inject * &#39; &quot; &#x3D;end  rn n r?: string, options?: any) {
        return FakeApiFp(this.configuration).testCodeInjectEndRnNR(test code inject * &#39; &quot; &#x3D;end  rn n r, options)(this.fetch, this.basePath);
    }

}
