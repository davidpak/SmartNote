from typing import *

SwitchValue = Union[None, str, int, float, bool, Callable[[], Union[int, None]]]
SwitchType = Union[None, type[str], type[int], type[float], type[bool]]

class Switch:
    """
    Command line switch.
    """

    def __init__(self,
                 name: str,
                 short: Union[str, None],
                 description: Union[str, None]=None,
                 value: SwitchValue=None,
                 type: SwitchType=None):
        """
        Create a new switch.

        Parameters:
        - `name`: Switch name.
        - `short`: Short switch name, e.g. `'h'` for `'--help'`. If
                   `None`, then the switch has no short name.
        - `description`: Switch description.
        - `value`: Switch value.
        - `type`: Switch type.
        """

        self.name = name
        self.short = short
        self.description = description
        self.value = value
        self.type = type

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


def parse_command_line(argv: list[str],
                       switches: Union[list[Switch],None]=None
                       ) -> Union[tuple[list[str], dict[str, SwitchValue]], int]:
    """
    Parse command line arguments.

    Parameters:
    - `argv`: Command line arguments, including program name.
    - `switches`: List of Switch objects representing command line
                  switches, if None then no switches are parsed.

    Returns:
    Either a tuple of `(args, options)` or an exit code. `args` is a
    list of non-switch arguments and `options` is a dict of switch
    values that were parsed.
    """

    result_switches: dict[str, SwitchValue] = {}
    
    i = 1
    if switches is not None:
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
            
            # add to result
            result_switches[switch.name] = switch.value
            
    return (argv[i:], result_switches)
