/**
 * Config file to switch between different types of applications. Ex. Hand Hygiene, Blue Gloves, or Blue Masks.
 */
export class Config {
    appName: string;
    websiteUrl: string;
    logoURL: string;

    constructor(appName: string, websiteUrl: string, logoURL: string) {
        this.appName = appName;
        this.websiteUrl = websiteUrl;
        this.logoURL = logoURL;
    }

    public setConfig(config: Config) {
        this.appName = config.appName;
        this.websiteUrl = config.websiteUrl;
        this.logoURL = config.logoURL;
    }
}
