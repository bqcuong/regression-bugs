# NOTE: This class is auto generated by the swagger code generator program.
# https://github.com/swagger-api/swagger-codegen.git
# Do not edit the class manually.

defmodule SwaggerPetstore.Model.Animal do
  @moduledoc """
  
  """

  @derive [Poison.Encoder]
  defstruct [
    :"className",
    :"color"
  ]
end

defimpl Poison.Decoder, for: SwaggerPetstore.Model.Animal do
  def decode(value, _options) do
    value
  end
end
