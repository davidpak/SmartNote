from typing import *
import argparse

SwitchValue = Union[None, str, int, float, bool, Callable[[], Union[int, None]]]
SwitchType = Union[None, type[str], type[int], type[float], type[bool]]

class Switch:
    """
    Command line switch. Provides a way to specify command line options
    and flags. Also supports custom handling of a switch, e.g. to
    display a usage message on `--help`.
    """

    def __init__(self,
                 name: str,
                 short: Union[str, None]=None,
                 value: SwitchValue=None,
                 type: SwitchType=None):
        """
        Create a new switch.

        Parameters:
        - `name`: Switch name. Does not include the leading `--` or `-`.
        - `short`: Short switch name, e.g. `'h'` for `'--help'`. Short
                   names are identified by a single leading `-`, but
                   this argument should not include it. If this is `None`,
                   then the switch's short name will be the first letter
                   of its name. If there are duplicate short names, then
                   the last switch with a given short name will be used.
        - `value`: Default switch value. `None` means no default value
                   and the switch `type` must be specified.
        - `type`: Switch type. `None` means no type and the switch
                  `value` must be specified. This is only `None` if
                  `value` is callable (e.g. for a help switch).
        """

        self.name = name
        self.short = name[0] if short is None else short
        self.value = value
        self.type = type

        if self.type is None and self.value is None:
            raise Exception('Switch must have a type or value')
        
        if callable(self.value) and self.type is not None:
            raise Exception('Switch cannot have a type and a callable value')

    def handle(self, argv: list[str], i: int) -> tuple[int, Union[int, None]]:
        """
        Handle a switch.

        Parameters:
        - `argv`: Command line arguments.
        - `i`: Index of switch in `argv`, not including switch name.

        Returns:
        A tuple of `(i, rc)` where `i` is the index of the next
        argument  to process and `rc` is the return code from the
        switch value function, if any. If `rc` is not `None`, then
        the command line should not be processed further.
        """

        if self.type is None:
            if callable(self.value):
                rc = self.value()
                return (i, rc)

        if i >= len(argv):
            if self.type == bool:
                self.value = True
                return (i, None)
            else:
                raise Exception(f'Missing value for switch {self.name}')

        arg = argv[i]
        rc = None

        if arg.startswith('-'):
            if self.type == bool:
                self.value = True
                return (i, None)
            else:
                raise Exception(f'Missing value for switch {self.name}')
        elif self.type == str:
            self.value = arg
            i = i + 1
        elif self.type == int:
            self.value = int(arg)
            i = i + 1
        elif self.type == float:
            self.value = float(arg)
            i = i + 1
        elif self.type == bool:
            self.value = True
        else:
            raise Exception(f'Unknown switch type: {self.type}')
        
        return (i, rc)

    def handle_summarize(self, args) -> None:
        """
        Handle the switch by updating its value in the args namespace.

        Parameters:
        - `args`: Namespace object containing command-line argument values.
        """
        value = getattr(args, self.name, self.value)

        if callable(value):
            value = value()

        setattr(args, self.name, value)


def parse(argv: list[str],
          switches: Union[list[Switch],None]=None
          ) -> Union[tuple[list[str], dict[str, SwitchValue]], int]:
    """
    Parse command line arguments.

    Parameters:
    - `argv`: Command line arguments, including program name. The program
              name is ignored.
    - `switches`: List of `Switch` objects representing command line
                  switches. Specifiying `None` means no switches and
                  is the same as specifying an empty `list`.

    Returns:
    Either a tuple of `(args, options)` or an exit code, as an `int`.
    `args` is a `list` of non-switch arguments and `options` is a
    `dict` of switch values that were parsed.
    """

    result_switches: dict[str, SwitchValue] = {}

    i = 1
    if switches is not None:
        # Initialize switches with default values
        for switch in switches:
            result_switches[switch.name] = switch.value

        # place switches into dict for easy lookup
        switch_dict: dict[str, Switch] = {}
        for switch in switches:
            switch_dict['--' + switch.name] = switch
            if switch.short is not None:
                switch_dict['-' + switch.short] = switch

        while i < len(argv):
            arg = argv[i]
            switch = switch_dict.get(arg)
            if switch is None:
                if arg.startswith('-'):
                    raise Exception(f'Unknown switch: {arg}')
                break

            # process switch
            i, rc = switch.handle(argv, i + 1)
            if rc is not None:
                return rc
            
            result_switches[switch.name] = switch.value

    return (argv[i:], result_switches)


def parse_summarize(argv: list[str], switches: Union[list[Switch], None] = None) -> Union[argparse.Namespace, dict]:
    """
    Parse command line arguments.

    Parameters:
    - `argv`: Command line arguments, including the program name. The program name is ignored.
    - `switches`: List of Switch objects representing command line switches. None means no switches, equivalent to an empty list.

    Returns:
    Either a Namespace object containing parsed arguments or a dictionary of switch values.
    """
    result_switches = {}

    parser = argparse.ArgumentParser(description='Script summarizer')

    if switches is not None:
        for switch in switches:
            parser.add_argument(f'-{switch.short}', f'--{switch.name}', type=switch.type, default=switch.value,
                                help=f'Description for {switch.name} (default: {switch.value})')

    args, _ = parser.parse_known_args(argv)

    if switches is not None:
        for switch in switches:
            switch.handle_summarize(args)
            result_switches[switch.name] = getattr(args, switch.name)

    return args, result_switches

