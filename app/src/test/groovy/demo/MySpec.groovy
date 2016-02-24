package demo

import spock.lang.Specification

class MySpec extends Specification {

    void 'test transformation'() {
        given:
        def helper = new MyHelper()

        expect:
        helper.someMethod(new Widget())
        helper.someClosure(new Widget())
    }
}

@MyAnnotation
class MyHelper {

    def someClosure = { Widget w ->
        w.wasActivated
    }
    boolean someMethod(Widget w) {
        w.wasActivated
    }
}

class Widget {
    boolean wasActivated

    void activate() {
        wasActivated = true
    }
}
