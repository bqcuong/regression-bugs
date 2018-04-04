/**
 * Config file to switch between different types of applications. Ex. Hand Hygiene, Blue Gloves, or Blue Masks.
 */
export class Config {
    servicesReturnFakeData = true;

    appName: string;
    websiteUrl: string;
    bannerURL: string;
    backendPort: string;

    constructor(appName: string, websiteUrl: string, logoURL: string, backendPort: string) {
        this.appName = appName;
        this.websiteUrl = websiteUrl;
        this.bannerURL = logoURL;
        this.backendPort = backendPort;
    }

    public setConfig(config: Config) {
        this.appName = config.appName;
        this.websiteUrl = config.websiteUrl;
        this.bannerURL = config.bannerURL;
        this.backendPort = config.backendPort;
    }
}
