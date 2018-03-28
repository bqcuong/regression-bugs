/**
 * Config file to switch between different types of applications. Ex. Hand Hygiene, Blue Gloves, or Blue Masks.
 */
export class Config {
    navbarTitle: string;
    websiteUrl: string;

    constructor(navbarTitle: string, websiteUrl: string) {
        this.navbarTitle = navbarTitle;
        this.websiteUrl = websiteUrl;
    }

    public setConfig(config: Config) {
        this.navbarTitle = config.navbarTitle;
        this.websiteUrl = config.websiteUrl;
    }
}
