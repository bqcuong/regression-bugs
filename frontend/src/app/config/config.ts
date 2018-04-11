/**
 * Config file to switch between different types of applications. Ex. Hand Hygiene, Blue Gloves, or Blue Masks.
 */
export class Config {
    servicesReturnFakeData = false;

    appName: string;
    websiteUrl: string;
    bannerUrl: string;
    logoUrl: string;
    backendHostname: string;
    backendPort: number;

    constructor(appName: string,
                websiteUrl: string,
                bannerUrl: string,
                logoUrl: string,
                backendHostname: string,
                backendPort: number) {
        this.appName = appName;
        this.websiteUrl = websiteUrl;
        this.bannerUrl = bannerUrl;
        this.logoUrl = logoUrl;
        this.backendHostname = backendHostname;
        this.backendPort = backendPort;
    }

    public setConfig(config: Config) {
        this.appName = config.appName;
        this.websiteUrl = config.websiteUrl;
        this.bannerUrl = config.bannerUrl;
        this.logoUrl = config.logoUrl;
        this.backendHostname = config.backendHostname;
        this.backendPort = config.backendPort;
    }

    public getBackendUrl() {
        return this.backendHostname + ':' + this.backendPort;
    }
}
