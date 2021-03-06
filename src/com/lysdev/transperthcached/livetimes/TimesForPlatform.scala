package com.lysdev.transperthcached.livetimes

class TimesForPlatform(
        number: Int,
        code: String,
        stop_number: String,
        position: String,
        times: List[Trip] ) {

    def getNumber()     = this.number
    def getCode()       = this.code
    def getStopNumber() = this.stop_number
    def getPosition()   = this.position
    def getTimes()      = this.times

    override def toString() = f"Platform $getCode at $getPosition with stop number $getStopNumber"
}
