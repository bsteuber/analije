(ns externs.cljasmine
  (:require [analije.generate-externs :as gen]))

(def jasmine-props
  '[not
    toBe
    toEqual
    toMatch
    toBeDefined
    toBeUndefined
    toBeNull
    toBeTruthy
    toBeFalsy
    toContain
    toBeLessThen
    toBeGreaterThen
    toBeCloseTo
    toThrow
    toHaveBeenCalled
    toHaveBeenCalledWith
    mostRecentCall
    calls
    args
    andCallThrough
    andReturn
    andCallFake
    createSpy
    createSpyObj
    addMatchers
    actual
    message])

(def jasmine-jquery-props
  '[toBe
    toBeChecked
    toBeEmpty
    toBeHidden
    toHaveCss
    toBeSelected
    toBeVisible
    toContain
    toExist
    toHaveAttr
    toHaveProp
    toHaveBeenTriggeredOn
    toHaveBeenTriggered
    toHaveBeenPreventedOn
    toHaveBeenPrevented
    toHaveClass
    toHaveData
    toHaveHtml
    toContainHtml
    toHaveId
    toHaveText
    toHaveValue
    toBeDisabled
    toBeFocused
    toHandle
    toHandleWith
    reset])

(def cljasmine-props
  '[_EQ_
    not_EQ_
    _LT_
    _LT__EQ_
    _GT_
    _GT__EQ_
    contains])

(defn gen [file]
  (gen/generate file "Cljasmine"
                (concat jasmine-props
                        jasmine-jquery-props
                        cljasmine-props)))
