package com.msgilligan.bitcoinj.cli;

import com.msgilligan.bitcoinj.rpc.JsonRPCException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * An attempt at cloning the bitcoin-cli tool, but using Java and bitcoinj
 *
 */
public class BitcoinJCli extends CliCommand {
    public final static String commandName = "bitcoinj-cli";

    public BitcoinJCli(String[] args) {
        super(commandName, new CliOptions(), args);
    }

    public static void main(String[] args) {
        BitcoinJCli command = new BitcoinJCli(args);
        Integer status = command.run();
        System.exit(status);
    }

    public Integer runImpl() throws IOException {
        List<String> args = line.getArgList();
        if (args.size() == 0) {
            printError("rpc method required");
            printHelp();
            return(1);
        }
        String method = args.get(0);
        args.remove(0); // remove method from list
        List<Object> typedArgs = convertParameters(method, args);
        Object result;
        try {
            result = client.cliSend(method, typedArgs.toArray());
        } catch (JsonRPCException e) {
            e.printStackTrace();
            return 1;
        }
        if (result != null) {
            pwout.println(result.toString());
        }
        return 0;
    }

    /**
     * Convert params from strings to Java types that will map to correct JSON types
     *
     * TODO: Make this better and complete
     *
     * @param method the JSON-RPC method
     * @param params Params with String type
     * @return Params with correct Java types for JSON
     */
    protected List<Object> convertParameters(String method, List<String> params) {
        List<Object> typedParams = new ArrayList<>();
        switch (method) {
            case "setgenerate":
                typedParams.add(0, Boolean.parseBoolean(params.get(0)));
                break;

            default:
                // Default (for now) is to leave them all as strings
                for (String string : params) {
                    typedParams.add(string);
                }

        }
        return typedParams;
    }
}
