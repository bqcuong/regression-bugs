/**
 * Config file to switch between different types of applications. Ex. Hand Hygiene, Blue Gloves, or Blue Masks.
 */
export class Config {
    servicesReturnFakeData = true;

    appName: string;
    websiteUrl: string;
    bannerURL: string;
    faviconURL: string;

    constructor(appName: string, websiteUrl: string, logoURL: string) {
        this.appName = appName;
        this.websiteUrl = websiteUrl;
        this.bannerURL = logoURL;
    }

    public setConfig(config: Config) {
        this.appName = config.appName;
        this.websiteUrl = config.websiteUrl;
        this.bannerURL = config.bannerURL;
    }
}
