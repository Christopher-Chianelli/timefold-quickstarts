from .json_serialization import *
from .domain import *


class MatchAnalysisDTO(JsonDomainBase):
    name: str
    score: Annotated[HardMediumSoftDecimalScore, ScoreSerializer]
    justification: object


class ConstraintAnalysisDTO(JsonDomainBase):
    name: str
    weight: Annotated[HardMediumSoftDecimalScore, ScoreSerializer]
    matches: list[MatchAnalysisDTO]
    score: Annotated[HardMediumSoftDecimalScore, ScoreSerializer]