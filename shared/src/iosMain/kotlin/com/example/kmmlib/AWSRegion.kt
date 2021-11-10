package com.example.kmmlib

import cocoapods.AWSCore.AWSRegionType

actual enum class AWSRegion(val region: AWSRegionType) {
    GovCloud(AWSRegionType.AWSRegionUSGovWest1),
    US_GOV_EAST_1(AWSRegionType.AWSRegionUSGovEast1),
    US_EAST_1(AWSRegionType.AWSRegionUSEast1),
    US_EAST_2(AWSRegionType.AWSRegionUSEast2),
    US_WEST_1(AWSRegionType.AWSRegionUSWest1),
    US_WEST_2(AWSRegionType.AWSRegionUSWest2),
    EU_SOUTH_1(AWSRegionType.AWSRegionEUSouth1),
    EU_WEST_1(AWSRegionType.AWSRegionEUWest1),
    EU_WEST_2(AWSRegionType.AWSRegionEUWest2),
    EU_WEST_3(AWSRegionType.AWSRegionEUWest3),
    EU_CENTRAL_1(AWSRegionType.AWSRegionEUCentral1),
    EU_NORTH_1(AWSRegionType.AWSRegionEUNorth1),
    AP_EAST_1(AWSRegionType.AWSRegionAPEast1),
    AP_SOUTH_1(AWSRegionType.AWSRegionAPSouth1),
    AP_SOUTHEAST_1(AWSRegionType.AWSRegionAPSoutheast1),
    AP_SOUTHEAST_2(AWSRegionType.AWSRegionAPSoutheast2),
    AP_NORTHEAST_1(AWSRegionType.AWSRegionAPNortheast1),
    AP_NORTHEAST_2(AWSRegionType.AWSRegionAPNortheast2),
    SA_EAST_1(AWSRegionType.AWSRegionSAEast1),
    CA_CENTRAL_1(AWSRegionType.AWSRegionCACentral1),
    CN_NORTH_1(AWSRegionType.AWSRegionCNNorth1),
    CN_NORTHWEST_1(AWSRegionType.AWSRegionCNNorthWest1),
    ME_SOUTH_1(AWSRegionType.AWSRegionMESouth1),
    AF_SOUTH_1(AWSRegionType.AWSRegionAFSouth1),
    DEFAULT_REGION(AWSRegionType.AWSRegionUnknown)
}