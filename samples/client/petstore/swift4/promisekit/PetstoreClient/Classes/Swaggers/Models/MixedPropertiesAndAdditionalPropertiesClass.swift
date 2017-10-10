//
// MixedPropertiesAndAdditionalPropertiesClass.swift
//
// Generated by swagger-codegen
// https://github.com/swagger-api/swagger-codegen
//

import Foundation


open class MixedPropertiesAndAdditionalPropertiesClass: Codable {

    public var uuid: UUID?
    public var dateTime: Date?
    public var map: [String:Animal]?

    public init() {}


    private enum CodingKeys: String, CodingKey { 
        case uuid = "uuid"
        case dateTime = "dateTime"
        case map = "map"
    }

}