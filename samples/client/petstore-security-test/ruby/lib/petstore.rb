=begin
#Swagger Petstore */ ' \" =_end -- \\r\\n \\n \\r

#This spec is mainly for testing Petstore server and contains fake endpoints, models. Please do not use this for any other purpose. Special characters: \" \\  */ ' \" =_end --       

OpenAPI spec version: 1.0.0 */ &#39; \&quot; &#x3D;_end -- \\r\\n \\n \\r
Contact: apiteam@swagger.io */ ' \" =_end -- \\r\\n \\n \\r
Generated by: https://github.com/swagger-api/swagger-codegen.git

=end

# Common files
require 'petstore/api_client'
require 'petstore/api_error'
require 'petstore/version'
require 'petstore/configuration'

# Models
require 'petstore/models/model_return'

# APIs
require 'petstore/api/fake_api'

module Petstore
  class << self
    # Customize default settings for the SDK using block.
    #   Petstore.configure do |config|
    #     config.username = "xxx"
    #     config.password = "xxx"
    #   end
    # If no block given, return the default Configuration object.
    def configure
      if block_given?
        yield(Configuration.default)
      else
        Configuration.default
      end
    end
  end
end