# NOTE: This class is auto generated by the swagger code generator program.
# https://github.com/swagger-api/swagger-codegen.git
# Do not edit the class manually.

defmodule SwaggerPetstore.Model.Client do
  @moduledoc """
  
  """

  @derive [Poison.Encoder]
  defstruct [
    :"client"
  ]
end

defimpl Poison.Decoder, for: SwaggerPetstore.Model.Client do
  def decode(value, _options) do
    value
  end
end
