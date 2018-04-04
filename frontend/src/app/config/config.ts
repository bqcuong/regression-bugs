/**
 * Config file to switch between different types of applications. Ex. Hand Hygiene, Blue Gloves, or Blue Masks.
 */
export class Config {
    servicesReturnFakeData = true;

    appName: string;
    websiteUrl: string;
    bannerURL: string;
    backendURL: string;
    backendPort: number;

    constructor(appName: string, websiteUrl: string, logoURL: string, backendURL: string, backendPort: number) {
        this.appName = appName;
        this.websiteUrl = websiteUrl;
        this.bannerURL = logoURL;
        this.backendURL = backendURL;
        this.backendPort = backendPort;
    }

    public setConfig(config: Config) {
        this.appName = config.appName;
        this.websiteUrl = config.websiteUrl;
        this.bannerURL = config.bannerURL;
        this.backendURL = config.backendURL;
        this.backendPort = config.backendPort;
    }
}
